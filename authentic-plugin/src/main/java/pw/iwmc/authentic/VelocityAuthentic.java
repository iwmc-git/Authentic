package pw.iwmc.authentic;

import com.google.inject.Inject;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;

import noelle.configuration.yaml.YamlLoader;
import noelle.features.languages.common.AbstractLanguages;
import noelle.features.languages.common.backend.BackendType;
import noelle.features.languages.common.key.LanguageKey;
import noelle.features.languages.common.language.Language;
import noelle.features.languages.velocity.VelocityLanguages;

import org.slf4j.Logger;

import pw.iwmc.authentic.api.Authentic;
import pw.iwmc.authentic.api.engine.storage.StorageType;
import pw.iwmc.authentic.configuration.PluginConfiguration;
import pw.iwmc.authentic.engine.PluginEngine;
import pw.iwmc.authentic.engine.storage.PluginStorage;
import pw.iwmc.authentic.listeners.GameProfileRequestListener;

import pw.iwmc.libman.api.LibmanAPI;
import pw.iwmc.libman.api.objects.Dependency;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class VelocityAuthentic implements Authentic {
    private static VelocityAuthentic authentic;

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path rootPath;

    private ScheduledTask uploadCacheTask;
    private ScheduledTask reconnectTask;

    private PluginConfiguration configuration;
    private PluginEngine engine;
    private PluginStorage storage;

    private PluginDescription description;

    private AbstractLanguages<Player> languages;

    @Inject
    public VelocityAuthentic(ProxyServer proxyServer, Logger logger, @DataDirectory Path rootPath) {
        authentic = this;

        this.proxyServer = proxyServer;
        this.logger = logger;
        this.rootPath = rootPath;
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onProxyInit(ProxyInitializeEvent event) {
        this.description = this.proxyServer.getPluginManager().fromInstance(this).get().getDescription();

        var formattedMessage = String.format("%s v%s is loading now...", this.description.getName().get(), this.description.getVersion().get());
        this.logger.info(formattedMessage);

        this.prepare();
        this.loadConfiguration();
        this.validateDatabase();

        var defaultLanguageRaw = configuration.defaultLanguage();
        var optionalLanguage = Language.fromKey(LanguageKey.of(defaultLanguageRaw));

        if (optionalLanguage.isEmpty()) {
            logger.error("Invalid locale detected - " + defaultLanguageRaw);
            return;
        }

        this.languages = VelocityLanguages.init(
                BackendType.YAML,
                rootPath,
                optionalLanguage.get(),
                VelocityAuthentic.class,
                "default-authentic.yaml"
        );

        this.languages.init();

        this.engine = new PluginEngine();
        this.storage = new PluginStorage();

        this.uploadCacheTask = proxyServer.getScheduler()
                .buildTask(authentic, () -> engine.uploadCache())
                .delay(configuration.caching().cachingTime())
                .repeat(configuration.caching().cachingTime())
                .schedule();

        this.reconnectTask = proxyServer.getScheduler()
                .buildTask(this, () -> storage.reconnect())
                .delay(10, TimeUnit.MINUTES)
                .repeat(10, TimeUnit.MINUTES)
                .schedule();

        var eventManager = proxyServer.getEventManager();
        eventManager.register(this, new GameProfileRequestListener());
    }

    @Subscribe(order = PostOrder.EARLY)
    public void onProxyShutdown(ProxyShutdownEvent event) {
        var formattedMessage = String.format("%s v%s is stopping now...", this.description.getName().get(), this.description.getVersion().get());
        this.logger.info(formattedMessage);

        reconnectTask.cancel();
        uploadCacheTask.cancel();

        storage.close();
    }

    private void validateDatabase() {
        var storage = configuration.storage();
        var type = storage.storageType();

        if (type == StorageType.MARIADB) {
            var username = storage.user();
            if (username.equalsIgnoreCase("username")) {
                logger.error("Database section contains default value! (username: username)");
                return;
            }

            var password = storage.password();
            if (password.equalsIgnoreCase("password")) {
                logger.error("Database section contains default value! (password: password)");
                return;
            }

            var database = storage.database();
            if (database.equalsIgnoreCase("database")) {
                logger.error("Database section contains default value! (database: database)");
                return;
            }
        }
    }

    private void prepare() {
        this.logger.info("Downloading plugin dependencies...");

        var caffeineDependency = Dependency.of("com.github.ben-manes.caffeine:caffeine:3.1.1");
        var libman = LibmanAPI.libman();

        var downloaded = libman.downloaded();
        var downloader = libman.downloader();

        downloader.downloadDependency(caffeineDependency);

        this.logger.info("Injecting dependencies...");
        downloaded.forEach((key, value) -> inject(value));
    }

    private void inject(Path path) {
        this.proxyServer.getPluginManager().addToClasspath(this, path);
    }

    private void loadConfiguration() {
        this.logger.info("Loading configuration...");

        try {
            if (Files.notExists(rootPath)) {
                Files.createDirectory(rootPath);
            }

            var resource = getClass().getClassLoader().getResourceAsStream("default-authentic.yaml");
            if (resource == null) {
                throw new RuntimeException("Default configuration not found!");
            }

            var configFile = rootPath.resolve("authentic.yaml");
            if (Files.notExists(configFile)) {
                Files.copy(resource, configFile);
            }

            this.logger.info("Mapping configuration via object mapper...");
            this.configuration = YamlLoader.loader(configFile).configuration().value(PluginConfiguration.class);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public static VelocityAuthentic authentic() {
        return authentic;
    }

    public Logger logger() {
        return logger;
    }

    public Path rootPath() {
        return rootPath;
    }

    public ProxyServer proxyServer() {
        return proxyServer;
    }

    public AbstractLanguages<Player> languages() {
        return languages;
    }

    public PluginStorage storage() {
        return storage;
    }

    @Override
    public PluginEngine engine() {
        return engine;
    }

    @Override
    public PluginConfiguration configuration() {
        return configuration;
    }
}
