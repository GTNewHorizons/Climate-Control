package climateControl.customGenLayer;

import climateControl.BiomeRandomizer;
import climateControl.utils.IntRandomizer;
import climateControl.ClimateControl;

import climateControl.utils.Zeno410Logger;
import java.util.logging.Logger;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
/**
 *
 * @author MasterCaver modified by Zeno410
 */
public class GenLayerBiomeByClimate extends GenLayer {
    //public static Logger logger = new Zeno410Logger("GenLayerBiomeByClimate").logger();

    private BiomeRandomizer biomeRandomizer;

    private IntRandomizer randomCallback;

    private BiomeRandomizer.PickByClimate pickByClimate;

    public GenLayerBiomeByClimate(long par1, GenLayer par3GenLayer){
        super(par1);
        this.parent = par3GenLayer;

        /*this.randomBiomeList = new BiomeGenBase[] {BiomeGenBase.desert, BiomeGenBase.desert, BiomeGenBase.savanna,
            BiomeGenBase.plains, BiomeGenBase.plains, BiomeGenBase.forest, BiomeGenBase.forest,
            BiomeGenBase.roofedForest, BiomeGenBase.extremeHills, BiomeGenBase.extremeHills,
            BiomeGenBase.birchForest, BiomeGenBase.swampland, BiomeGenBase.swampland, BiomeGenBase.taiga,
            BiomeGenBase.icePlains, BiomeGenBase.coldTaiga, BiomeGenBase.mesaPlateau, BiomeGenBase.mesaPlateau_F,
            BiomeGenBase.megaTaiga, BiomeGenBase.jungle, BiomeGenBase.jungle};*/


        biomeRandomizer = BiomeRandomizer.instance;
        pickByClimate = biomeRandomizer.pickByClimate();
        randomCallback = new IntRandomizer() {
            public int nextInt(int maximum) {
                return GenLayerBiomeByClimate.this.nextInt(maximum);
            }
        };
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] aint = this.parent.getInts(par1, par2, par3, par4);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; i1++)
        {
            for (int j1 = 0; j1 < par3; j1++)
            {
                this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                int k1 = aint[j1 + i1 * par3];
                k1 &= -3841;
                    if (k1 > 256) {
                        if (ClimateControl.testing) {
                        ClimateControl.logger.info(parent.toString());
                        ClimateControl.logger.info("number "+k1+ " from "+aint[j1 + i1 * par3]);
                    throw new RuntimeException();
                    }
                }
                //ClimateControl.logger.info(""+k1);

                if ((isBiomeOceanic(k1))&&(k1 != BiomeGenBase.deepOcean.biomeID)){
                    aint1[j1 + i1 * par3] = k1;
                }
                else if (k1 == BiomeGenBase.mushroomIsland.biomeID){
                    aint1[j1 + i1 * par3] = k1;
                }
                else {
                    aint1[j1 + i1 * par3] = pickByClimate.biome(k1, randomCallback);
                    //logger.info("("+(i1+par2)+","+(j1+par1)+") Climate "+k1 + " " + aint[j1 + i1 * par3]+" Biome " + aint1[j1 + i1 * par3]);

                }
            }
        }

        return aint1;
    }
}