
package climatecontrol.biomeSettings;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

import climatecontrol.api.BiomeSettings;
import climatecontrol.api.ClimateControlRules;
import climatecontrol.utils.Mutable;
import climatecontrol.utils.Settings;

/**
 *
 * @author Zeno410
 */

public class GrowthcraftBiomeSettings extends BiomeSettings {

    public static final String biomeCategory = "GrowthcraftBiome";
    public static final String growthcraftCategory = "GrowthcrafSettings";
    public final Category growthcraftSettings = new Category(growthcraftCategory);

    public final Element bambooForest = new Element("Bamboo Forest", 170, "WARM");

    public GrowthcraftBiomeSettings() {
        super(biomeCategory);
    }

    @Override
    public void setRules(ClimateControlRules rules) {
        // no action
    }

    @Override
    public void setNativeBiomeIDs(File configDirectory) {
        GrowthcraftSettings result = new GrowthcraftSettings();
        File arsMagicaDirectory = new File(configDirectory, "growthcraft");
        File configFile = new File(arsMagicaDirectory, "bamboo.conf");
        result.readFrom(new Configuration(configFile));
        bambooForest.biomeID()
            .set(result.bambooForestID.value());
    }

    static final String biomesOnName = "GrowthcraftBiomesOn";

    public final Mutable<Boolean> biomesFromConfig = climateControlCategory.booleanSetting(biomesOnName, "", false);

    @Override
    public boolean biomesAreActive() {
        return this.biomesFromConfig.value();
    }

    static final String configName = "Growthcraft";
    public final Mutable<Boolean> biomesInNewWorlds = climateControlCategory
        .booleanSetting(this.startBiomesName(configName), "Use biome in new worlds and dimensions", true);

    @Override
    public void onNewWorld() {
        biomesFromConfig.set(biomesInNewWorlds);
    }

    private class GrowthcraftSettings extends Settings {

        public static final String biomeIDName = "biomes";
        public final Category biomeIDs = new Category(biomeIDName);

        Mutable<Integer> bambooForestID = biomeIDs.intSetting("Bamboo Forest biome ID", 170);

    }
}
