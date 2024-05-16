
package climateControl.generator;

import net.minecraft.world.gen.layer.GenLayer;

import climateControl.genLayerPack.GenLayerPack;
import climateControl.utils.AccessLong;
import climateControl.utils.Accessor;

/**
 *
 * @author Zeno410
 */
public class TestGeneratorPair {

    private GenLayer oldGen;
    private GenLayer newGen;

    private Accessor<GenLayerPack, GenLayerPack> genLayerPackParent = new Accessor<GenLayerPack, GenLayerPack>(
        "field_75909_a",
        "parent");

    private Accessor<GenLayer, GenLayer> genLayerParent = new Accessor<GenLayer, GenLayer>("field_75909_a", "parent");

    private AccessLong<GenLayer> genLayerSeed = new AccessLong<GenLayer>("field_75907_b", "worldGenSeed");

    public TestGeneratorPair(GenLayer oldGen, GenLayer newGen) {
        this.oldGen = oldGen;
        this.newGen = newGen;
    }

    public TestGeneratorPair next() {
        return new TestGeneratorPair(parent(oldGen), parent(newGen));
    }

    public boolean hasNext() {
        return ((oldGen != null) || (newGen != null));
    }

    public GenLayer parent(GenLayer child) {
        if (child == null) return null;
        if (child instanceof GenLayerPack) {
            return ((GenLayerPack) child).getParent();
        }
        return genLayerParent.get(child);
    }

    public String description() {
        return description(oldGen) + " vs " + description(newGen);
    }

    private String description(GenLayer described) {
        if (described == null) return "missing";
        return described.getClass()
            .getSimpleName() + " "
            + genLayerSeed.get(described);
    }
}
