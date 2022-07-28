package pw.iwmc.authentic;

import com.google.inject.Inject;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;

import noelle.configuration.DefaultConfiguration;
import noelle.configuration.types.hocon.HoconLoader;
import noelle.configuration.types.yaml.YamlLoader;
import noelle.encryptor.Algorithm;
import noelle.encryptor.PasswordEncryptor;
import noelle.features.messages.common.AbstractMessages;
import noelle.features.messages.velocity.VelocityMessages;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pw.iwmc.authentic.api.Authentic;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.floodgate.FloodgateHolder;
import pw.iwmc.authentic.limbo.PluginLimbo;
import pw.iwmc.authentic.managers.PluginAccountManager;
import pw.iwmc.authentic.managers.PluginLicenseManager;
import pw.iwmc.authentic.managers.PluginStorageManager;

import pw.iwmc.authentic.messages.MessageKeys;
import pw.iwmc.libman.api.LibmanAPI;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VelocityAuthentic implements Authentic {
    private final Logger defaultLogger = LoggerFactory.getLogger("authentic::default");
    private final Logger debuggerLogger = LoggerFactory.getLogger("authentic::debug");

    private static VelocityAuthentic authentic;

    private final ProxyServer proxyServer;
    private final Path rootPath;

    private FloodgateHolder floodgateHolder;
    private PluginLimbo limbo;

    private PluginConfiguration configuration;
    private PluginAccountManager accountManager;
    private PluginLicenseManager licenseManager;
    private PluginStorageManager storageManager;

    private AbstractMessages<Player> messages;
    private PasswordEncryptor passwordEncryptor;

    private List<String> unsafePasswords;

    @Inject
    public VelocityAuthentic(ProxyServer proxyServer, @DataDirectory Path rootPath) {
        authentic = this;

        this.proxyServer = proxyServer;
        this.rootPath = rootPath;
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent event) {
        defaultLogger.info("Loading plugin...");

        injectDependencies();

        loadConfiguration();
        loadMessages();

        this.limbo = new PluginLimbo();

        this.accountManager = new PluginAccountManager();
        this.storageManager = new PluginStorageManager();
        this.licenseManager = new PluginLicenseManager();

        this.unsafePasswords = applyUnsafePasswords();
        this.passwordEncryptor = applyEncryptor();

        if (proxyServer.getPluginManager().getPlugin("floodgate").isPresent()) {
            this.floodgateHolder = new FloodgateHolder();
        }

        var eventManager = proxyServer.getEventManager();
        eventManager.register(this, new PluginListeners());
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        defaultLogger.info("Disabling plugin...");

        var cachedAccounts = accountManager.cachedAccounts();

        defaultLogger.info("Updating " + cachedAccounts.size() + " in database...");
        cachedAccounts.forEach((key, value) -> storageManager.updateAccount(value));

        storageManager.close();
    }

    public static VelocityAuthentic authentic() {
        return authentic;
    }

    public FloodgateHolder floodgateHolder() {
        return floodgateHolder;
    }

    public PluginLimbo limbo() {
        return limbo;
    }

    public List<String> unsafePasswords() {
        return unsafePasswords;
    }

    public AbstractMessages<Player> messages() {
        return messages;
    }

    public Path rootPath() {
        return rootPath;
    }

    public ProxyServer proxyServer() {
        return proxyServer;
    }

    public Logger defaultLogger() {
        return defaultLogger;
    }

    public Logger debuggerLogger() {
        return debuggerLogger;
    }

    public PasswordEncryptor passwordEncryptor() {
        return passwordEncryptor;
    }

    // =================== Private methods =================== //

    private PasswordEncryptor applyEncryptor() {
        var encryprtionMethod = configuration.securityConfiguration().encryptionMethod().name();
        var algorithm = Algorithm.valueOf(encryprtionMethod);
        return PasswordEncryptor.encryptionType(algorithm);
    }

    private List<String> applyUnsafePasswords() {
        try {
            var resource = getClass().getClassLoader().getResourceAsStream("pw/iwmc/authentic/files/unsafe-passwords.txt");
            if (resource == null) {
                throw new RuntimeException("unsafe password not found in JAR!!");
            }

            var unsafePasswordsPath = rootPath.resolve("unsafe-passwords.txt");
            if (Files.notExists(unsafePasswordsPath)) {
                Files.copy(resource, unsafePasswordsPath);
            }

            var passwordsList = Files.lines(unsafePasswordsPath).toList();
            defaultLogger.info("Applying " + passwordsList.size() + " usafe passwords...");

            return passwordsList;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    private void injectDependencies() {
        var libman = LibmanAPI.libman();
        var downloaded = libman.downloaded();

        defaultLogger.info("Injecting " + downloaded.size() + " dependencies...");
        downloaded.forEach((key, value) -> proxyServer.getPluginManager().addToClasspath(this, value));
    }

    private void loadMessages() {
        defaultLogger.info("Loading messages...");

        var messagesConfig = loadDefaultConfiguration("default-messages.yaml", "messages.yaml");
        var prefixKey = MessageKeys.PREFIX;

        this.messages = VelocityMessages.init(messagesConfig);
        this.messages.applyPrefix(prefixKey, "%prefix%");
    }

    private void loadConfiguration() {
        defaultLogger.info("Loading configuration...");

        var rawConfig = loadDefaultConfiguration("default-authentic.conf", "authentic.conf");
        this.configuration = rawConfig.value(PluginConfiguration.class);
    }

    private DefaultConfiguration<?> loadDefaultConfiguration(String includedName, String outputName) {
        try {
            if (Files.notExists(rootPath)) {
                defaultLogger.info("Creating plugin directory...");
                Files.createDirectory(rootPath);
            }

            var resource = getClass().getClassLoader().getResourceAsStream("pw/iwmc/authentic/files/" + includedName);
            if (resource == null) {
                throw new RuntimeException(includedName + " configuration not found!");
            }

            var configFile = rootPath.resolve(outputName);
            if (Files.notExists(configFile)) {
                defaultLogger.info("Copying configuration file - " + includedName);
                Files.copy(resource, configFile);
            }

            defaultLogger.info("Loading configuration file - " + outputName);
            if (includedName.endsWith(".conf") && outputName.endsWith(".conf")) {
                return HoconLoader.loader(configFile).configuration();
            } else {
                return YamlLoader.loader(configFile).configuration();
            }
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    // =================== API getter methods =================== //

    @Override
    public PluginConfiguration configuration() {
        return configuration;
    }

    @Override
    public PluginAccountManager accountManager() {
        return accountManager;
    }

    @Override
    public PluginStorageManager storageManager() {
        return storageManager;
    }

    @Override
    public PluginLicenseManager licenseManager() {
        return licenseManager;
    }
}
