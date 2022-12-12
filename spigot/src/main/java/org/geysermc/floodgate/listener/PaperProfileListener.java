/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Floodgate
 */

package org.geysermc.floodgate.listener;

import com.destroystokyo.paper.event.profile.PreFillProfileEvent;
import com.destroystokyo.paper.profile.ProfileProperty;
import com.google.inject.Inject;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.geysermc.floodgate.api.SimpleFloodgateApi;
import org.geysermc.floodgate.api.logger.FloodgateLogger;
import org.geysermc.floodgate.api.player.FloodgatePlayer;

public final class PaperProfileListener implements Listener {
    @Inject private SimpleFloodgateApi api;
    @Inject private FloodgateLogger logger;

    @EventHandler
    public void onFill(PreFillProfileEvent event) {
        UUID id = event.getPlayerProfile().getId();
        if (id == null) {
            return;
        }

        FloodgatePlayer floodgatePlayer = api.getPlayer(id);
        if (floodgatePlayer == null || floodgatePlayer.isLinked()) {
            return;
        }

        logger.info("event:");
        for (ProfileProperty prop : event.getPlayerProfile().getProperties()) {
            logger.info("\tname:\t" + prop.getName());
            logger.info("\tvalue:\t" + prop.getValue());
            logger.info("\tsignature:\t" + prop.getSignature());
        }

        logger.info("uuid: " + event.getPlayerProfile().getId());
        Player player = Bukkit.getPlayer(event.getPlayerProfile().getId());

        if (player != null) {
            logger.info("");
            logger.info("player:");
            for (ProfileProperty prop : player.getPlayerProfile().getProperties()) {
                logger.info("\tname:\t" + prop.getName());
                logger.info("\tvalue:\t" + prop.getValue());
                logger.info("\tsignature:\t" + prop.getSignature());
            }
        }

        // back when this event got added the PlayerProfile class didn't have the
        // hasProperty / hasTextures methods
        if (event.getPlayerProfile().getProperties().stream().anyMatch(
                prop -> "textures".equals(prop.getName()))) {
            return;
        }

        Set<ProfileProperty> properties = new HashSet<>(event.getPlayerProfile().getProperties());
        properties.add(new ProfileProperty("textures", "", ""));
        event.setProperties(properties);
    }
}
