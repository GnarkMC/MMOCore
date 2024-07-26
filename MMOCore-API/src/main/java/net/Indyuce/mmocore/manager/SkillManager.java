package net.Indyuce.mmocore.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import net.Indyuce.mmocore.skill.list.Ambers;
import net.Indyuce.mmocore.skill.list.Neptune_Gift;
import net.Indyuce.mmocore.skill.list.Sneaky_Picky;
import net.Indyuce.mmocore.util.FileUtils;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class SkillManager implements MMOCoreManager {
    private final Map<String, RegisteredSkill> skills = new LinkedHashMap<>();

    public void registerSkill(RegisteredSkill skill) {
        skills.put(skill.getHandler().getId().toUpperCase(), skill);
    }

    @Nullable
    public RegisteredSkill getSkill(String id) {
        return skills.get(id.toUpperCase());
    }

    @NotNull
    public RegisteredSkill getSkillOrThrow(String id) {
        return Objects.requireNonNull(skills.get(id), "Could not find skill with ID '" + id + "'");
    }

    public Collection<RegisteredSkill> getAll() {
        return skills.values();
    }

    public void initialize(boolean clearBefore) {
        if (clearBefore) {
            MythicLib.plugin.getSkills().initialize(true);
            skills.clear();
        }

        // Register MMOCore specific skills
        MythicLib.plugin.getSkills().registerSkillHandler(new Ambers());
        MythicLib.plugin.getSkills().registerSkillHandler(new Neptune_Gift());
        MythicLib.plugin.getSkills().registerSkillHandler(new Sneaky_Picky());

        // Save default files if necessary
        final File skillFolder = FileUtils.getFile(MMOCore.plugin, "skills");
        if (!skillFolder.exists()) try {
            skillFolder.mkdir();

            for (SkillHandler<?> handler : MythicLib.plugin.getSkills().getHandlers()) {
                final InputStream res = MMOCore.plugin.getResource("default/skills/" + handler.getLowerCaseId() + ".yml");
                if (res != null)
                    Files.copy(res, new File(MMOCore.plugin.getDataFolder() + "/skills/" + handler.getLowerCaseId() + ".yml").getAbsoluteFile().toPath());
            }
        } catch (IOException exception) {
            MMOCore.plugin.getLogger().log(Level.WARNING, "Could not save default skill configs: " + exception.getMessage());
        }

        // Load skills
        FileUtils.loadObjectsFromFolder(MMOCore.plugin, "skills", true, (name, config) -> {
            final SkillHandler<?> handler = MythicLib.plugin.getSkills().getHandler(UtilityMethods.enumName(name));
            Validate.notNull(handler, "Could not find skill handler with ID '" + UtilityMethods.enumName(name) + "'");
            final RegisteredSkill skill = new RegisteredSkill(handler, config);
            this.skills.put(handler.getId(), skill);
        }, "Could not load skill from file '%s': %s");
    }
}
