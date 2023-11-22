
package climatecontrol.biomeSettings;

import climatecontrol.api.BiomePackage;
import climatecontrol.api.BiomeSettings;
import thaumcraft.api.nodes.NodeType;

/**
 *
 * @author Zeno410
 */
public class ThaumcraftPackage extends BiomePackage {

    public ThaumcraftPackage() {
        super("ThaumcraftInCC.cfg");
        // confirm Thaumcraft is there.
        Class nodesClass = NodeType.class;
        NodeType type = NodeType.DARK;
    }

    @Override
    public BiomeSettings freshBiomeSetting() {
        return new ThaumcraftBiomeSettings();
    }

}
