/*
 *
 * Heroes PlaceholderAPI Expansion
 * Copyright (C) 2018 Ryan McCarthy
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *
 */
package com.ravingarinc.papi.expansion.heroes;

import com.herocraftonline.heroes.characters.party.HeroParty;
import com.herocraftonline.heroes.characters.skill.Skill;
import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;

public class HeroesExpansion extends PlaceholderExpansion implements Cacheable {

    private Heroes heroes;

    private final Map<String, Function<Hero, String>> exactMappings = new LinkedHashMap<>();
    private final Map<String, BiFunction<Hero, String, String>> paramMappings = new LinkedHashMap<>();
    private final Map<String, BiFunction<OfflinePlayer, String, String>> offlineMappings = new LinkedHashMap<>();

    @Override
    public boolean canRegister() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(getRequiredPlugin());
        if (plugin == null) return false;
        heroes = Heroes.getInstance();
        initialise();
        return true;
    }

    private void initialise() {
        addExactMapping("class_name", (hero) -> hero.getHeroClass().getName());
        addExactMapping("prof_name", (hero) -> hero.getSecondaryClass().getName());
        addExactMapping("race_name", (hero) -> hero.getRaceClass().getName());
        addExactMapping("primary_lvl", (hero) -> String.valueOf(hero.getHeroLevel(hero.getHeroClass())));
        addExactMapping("secondary_lvl", (hero) -> String.valueOf(hero.getHeroLevel(hero.getSecondaryClass())));
        addExactMapping("race_lvl", (hero) -> String.valueOf(hero.getHeroLevel(hero.getRaceClass())));

        addExactMapping("stamina", (hero) -> String.valueOf(hero.getStamina()));
        addExactMapping("max_stamina", (hero) -> String.valueOf(hero.getMaxStamina()));
        addExactMapping("mana", (hero) -> String.valueOf(hero.getMana()));
        addExactMapping("max_mana", (hero) -> String.valueOf(hero.getMaxMana()));
        addExactMapping("shield", (hero) -> String.valueOf(hero.getShield()));
        addExactMapping("max_shield", (hero) -> String.valueOf(hero.getMaxMana()));

        addOfflineMapping("skill_short_description_", (offline, param) -> {
            param = param.replace("skill_short_description_", "");
            Skill skill = heroes.getSkillManager().getSkill(param);
            if (skill == null) {
                Heroes.log(Level.WARNING, "Could not find skill '" + param + "' for Placeholder API Expansion, as that skill does not exist!");
                return null;
            }
            return skill.hasShortDescription() ? skill.getShortDescription() : "";
        });
        addOfflineMapping("skill_description_", (offline, param) -> {
            param = param.replace("skill_description_", "");
            Skill skill = heroes.getSkillManager().getSkill(param);
            if (skill == null) {
                Heroes.log(Level.WARNING, "Could not find skill '" + param + "' for Placeholder API Expansion, as that skill does not exist!");
                return null;
            }
            final Player player = offline.getPlayer();
            if(player == null) {
                return skill.getDescription();
            }
            final Hero hero = heroes.getCharacterManager().getHero(player);
            return skill.getDescription(hero);
        });

        addOfflineMapping("default_class", (offline, param) -> heroes.getClassManager().getDefaultClass().getName());
        addOfflineMapping("default_prof", (offline, param) -> heroes.getClassManager().getDefaultProfession().getName());
        addOfflineMapping("default_race", (offline, param) -> heroes.getClassManager().getDefaultRace().getName());

        addExactMapping("mastered_classes", (hero) -> String.valueOf(hero.getMasteredClasses().size()));

        addExactMapping("party_size", (hero) -> {
            final HeroParty party = hero.getParty();
            if(party == null) return "0";
            return String.valueOf(party.getMembers().size());
        });

        addMapping("party_leader_", (hero, param) -> {
            final HeroParty party = hero.getParty();
            if(party == null) return "";
            final String line = param.replace("party_leader_", "");
            final Hero leader = party.getLeader();
            switch (line) {
                case "name":
                    return leader.getName();
                case "health":
                    return String.valueOf((int) ((leader.getHealth() / leader.getMaxHealth()) * 100));
                case "mana":
                    return String.valueOf((int) ((leader.getMana() / (double) leader.getMaxMana()) * 100));
                case "stamina":
                    return String.valueOf((int) ((leader.getStamina() / (double) leader.getStamina()) * 100));
            }
            return null;
        });

        addMapping("skill_can_use_", (hero, param) -> {
            final String skillName = param.replace("skill_can_use_", "");
            Skill skill = heroes.getSkillManager().getSkill(param);
            if (skill == null) {
                Heroes.log(Level.WARNING, "Could not find skill '" + skillName + "' for Placeholder API Expansion, as that skill does not exist!");
                return null;
            }
            return String.valueOf(hero.canUseSkill(skill));
        });
    }

    private void addExactMapping(String key, Function<Hero, String> parser) {
        this.exactMappings.put(key, parser);
    }

    private void addMapping(String key, BiFunction<Hero, String, String> parser) {
        this.paramMappings.put(key, parser);
    }

    private void addOfflineMapping(String key, BiFunction<OfflinePlayer, String, String> parser) {
        this.offlineMappings.put(key, parser);
    }


    @Override
    @NotNull
    public String getIdentifier() {
        return "heroes";
    }

    @Override
    @NotNull
    public String getRequiredPlugin() {
        return "Heroes";
    }

    @Override
    @NotNull
    public String getAuthor() {
        return "RAVINGAR";
    }

    @Override
    @NotNull
    public String getVersion() {
        return "1.9.30";
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, final @NotNull String param) {
        final Player player = offlinePlayer.getPlayer();
        if(offlinePlayer.isOnline() && player != null) {
            final Hero hero = heroes.getCharacterManager().getHero(player);
            if(exactMappings.containsKey(param)) {
                return exactMappings.get(param).apply(hero);
            }
            for(Map.Entry<String, BiFunction<Hero, String, String>> entry : paramMappings.entrySet()) {
                if(param.startsWith(entry.getKey())) {
                    return entry.getValue().apply(hero, param);
                }
            }
        }
        for(Map.Entry<String, BiFunction<OfflinePlayer, String, String>> entry : offlineMappings.entrySet()) {
            if(param.startsWith(entry.getKey())) {
                return entry.getValue().apply(offlinePlayer, param);
            }
        }
        return null;
    }

    @Override
    public void clear() {
        heroes = null;
        paramMappings.clear();
    }
}
