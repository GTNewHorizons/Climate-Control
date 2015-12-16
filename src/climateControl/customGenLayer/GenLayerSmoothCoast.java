
package climateControl.customGenLayer;
import climateControl.genLayerPack.GenLayerPack;
import climateControl.utils.IntRandomizer;
import climateControl.utils.Zeno410Logger;
import java.util.logging.Logger;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerSmoothCoast extends GenLayerPack {

    public static Logger logger = new Zeno410Logger("SmoothCoast").logger();

    private static final String __OBFID = "CL_00000569";
    private static int sinkLand = 7;
    private static int raiseWater = 10;
    private LandWaterChoices choices = new LandWaterChoices();
    private IntRandomizer passable = new IntRandomizer() {

        @Override
        public int nextInt(int range) {
            return GenLayerSmoothCoast.this.nextInt(range);
        }

    };

    public GenLayerSmoothCoast(long par1, GenLayer par3GenLayer)
    {
        super(par1);
        super.parent = par3GenLayer;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int i1 = par1 - 1;
        int j1 = par2 - 1;
        int k1 = par3 + 2;
        int l1 = par4 + 2;
        int[] aint = this.parent.getInts(i1, j1, k1, l1);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i2 = 0; i2 < par4; ++i2){
            for (int j2 = 0; j2 < par3; ++j2){

                int original = aint[j2 + 1 + (i2 + 1) * k1];
                boolean isOceanic = this.isBiomeOceanic(original);
                choices.setOriginal(original, isOceanic);

                int up = aint[j2 + 0 + (i2 + 1) * k1];
                int down = aint[j2 + 2 + (i2 + 1) * k1];
                int left = aint[j2 + 1 + (i2 + 0) * k1];
                int right = aint[j2 + 1 + (i2 + 2) * k1];
                choices.add(up,isBiomeOceanic(up));
                choices.add(down,isBiomeOceanic(down));
                choices.add(right,isBiomeOceanic(right));
                choices.add(left,isBiomeOceanic(left));

                if (choices.equal()||(choices.isChoiceWater()==isOceanic)) {
                    aint1[j2 + i2 * par3] = original;
                    continue;
                }
                this.initChunkSeed((long)(j2 + par1), (long)(i2 + par2));
                int trip = (isOceanic) ? raiseWater :sinkLand;
                if (nextInt(10) < trip) {
                    // not equal, not similar to what we've got, and we want to change
                    aint1[j2 + i2 * par3] = choices.mostCommon(this.passable);
                    //logger.info("changed "+original +  " to "+ aint1[j2 + i2 * par3]);
                } else {
                    // keep what we've got
                    aint1[j2 + i2 * par3] = original;
                }
            }
        }

        return aint1;
    }
}