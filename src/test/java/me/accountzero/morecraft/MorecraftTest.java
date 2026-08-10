package me.accountzero.morecraft;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

// Mocks a server and loads the plugin via onEnable, exactly like a real server would.
public abstract class MorecraftTest {

    protected ServerMock server;
    protected Morecraft plugin;
    protected WorldMock world;

    @BeforeEach
    protected void setUpMorecraftServer() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(Morecraft.class);
        world = server.addSimpleWorld("world");
    }

    @AfterEach
    protected void tearDownMorecraftServer() {
        MockBukkit.unmock();
    }
}
