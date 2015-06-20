
package climateControl.customGenLayer;

import climateControl.BiomeRandomizer;
import climateControl.utils.IntRandomizer;
import climateControl.utils.Zeno410Logger;
import climateControl.generator.BiomeSwapper;
import climateControl.generator.SubBiomeChooser;
import climateControl.biomeSettings.BiomeReplacer;
import climateControl.biomeSettings.BoPSubBiomeReplacer;
import climateControl.generator.Decoder;
import java.util.logging.Logger;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class DecodingSubBiome extends GenLayer{
    public static Logger logger = new Zeno410Logger("GenLayerSubBiome").logger();
    private GenLayer rivers;
    private final SubBiomeChooser subBiomeChooser;
    private final BiomeSwapper mBiomes;
    private BiomeReplacer BoPSubBiomeReplacer;
    private final Decoder biomeDecoder;
    private final Decoder climateDecoder;
    private final BiomeRandomizer.PickByClimate pickByClimate;

    private IntRandomizer randomCallback = new IntRandomizer() {
       public int nextInt(int maximum) {
            return DecodingSubBiome.this.nextInt(maximum);
       }
    };

    private static final String __OBFID = "CL_00000563";

    public DecodingSubBiome(long p_i45479_1_, GenLayer rivers,
            SubBiomeChooser subBiomeChooser, BiomeSwapper mBiomes,
            Decoder biomeDecoder, Decoder climateDecoder,BiomeRandomizer.PickByClimate pickByClimate){
        super(p_i45479_1_);
        this.rivers = rivers;
        this.subBiomeChooser = subBiomeChooser;
        this.mBiomes = mBiomes;
        this.biomeDecoder = biomeDecoder;
        this.climateDecoder = climateDecoder;
        this.pickByClimate = pickByClimate;
        this.initChunkSeed(0, 0);
        try {
            BoPSubBiomeReplacer = new BoPSubBiomeReplacer(randomCallback);
            logger.info("Bop set up");
        } catch (java.lang.NoClassDefFoundError e) {
            BoPSubBiomeReplacer = null;
            logger.info("no bop ");
        }
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        //int[] biomeVals = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] riverVals = this.rivers.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] aint2 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1)
        {
            for (int j1 = 0; j1 < par3; ++j1){
                this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                int riverVal = riverVals[j1 + 1 + (i1 + 1) * (par3 + 2)];
                int biomeVal = this.biomeDecoder.decode(riverVal);
                boolean flag = (riverVal - 2) % 29 == 0;
                //logger.info("biome "+biomeVal);
                if (biomeVal != 0 &&biomeVal != BiomeGenBase.deepOcean.biomeID && riverVal >= 2 && (riverVal - 2) % 29 == 1 && biomeVal < 128) {
                     aint2[j1 + i1 * par3] = mBiomes.replacement(biomeVal);
                     //logger.info("Mbiome "+biomeVal + " to "+aint2[j1 + i1 * par3]);
                }
                else if (this.nextInt(3) != 0 && !flag) {
                    aint2[j1 + i1 * par3] = biomeVal;
                }
                else{
                    int i2 = biomeVal;
                    if (biomeVal != 0) {
                        i2 = this.subBiomeChooser.subBiome(biomeVal, randomCallback,j1 + par1,i1 + par2);
                    } else {
                        // special operations for coastal oceans
                        if (this.nextInt(3)==0) {
                            int climate = climateDecoder.decode(riverVal);
                            if (climate>0) {
                                boolean adjacentLand = false;
                                int j2 = biomeDecoder.decode(riverVals[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(j2)) adjacentLand = true;
                                int k2 = biomeDecoder.decode(riverVals[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(k2)) adjacentLand = true;
                                int l2 = biomeDecoder.decode(riverVals[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(l2)) adjacentLand = true;
                                int i3 = biomeDecoder.decode(riverVals[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(i3)) adjacentLand = true;
                                int corner = biomeDecoder.decode(riverVals[j1 + 1 +1 + (i1 + 1 + 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(corner)) adjacentLand = true;
                                corner = biomeDecoder.decode(riverVals[j1 + 1 - 1 + (i1 + 1 + 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(corner)) adjacentLand = true;
                                corner = biomeDecoder.decode(riverVals[j1 + 1 - 1 + (i1 + 1 - 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(corner)) adjacentLand = true;
                                corner = biomeDecoder.decode(riverVals[j1 + 1 + 1 + (i1 + 1 - 1) * (par3 + 2)]);
                                if (!isBiomeOceanic(corner)) adjacentLand = true;
                                if (!adjacentLand){
                                    i2 = pickByClimate.biome(climate, randomCallback);
                                }
                            }
                        }
                    }
                    int j2;

                    if (flag && i2 != biomeVal){
                        int newI2 = this.mBiomes.replacement(i2);
                        if (newI2 != i2){
                            i2 = newI2;
                        } else {
                            i2 = biomeVal;
                        }
                    }

                    if (i2 == biomeVal){
                        aint2[j1 + i1 * par3] = biomeVal;
                    }
                    else
                    {
                        j2 = biomeDecoder.decode(riverVals[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)]);
                        int k2 = biomeDecoder.decode(riverVals[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)]);
                        int l2 = biomeDecoder.decode(riverVals[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)]);
                        int i3 = biomeDecoder.decode(riverVals[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)]);
                        int j3 = 0;

                        if (compareBiomesById(j2, biomeVal))
                        {
                            ++j3;
                        }

                        if (compareBiomesById(k2, biomeVal))
                        {
                            ++j3;
                        }

                        if (compareBiomesById(l2, biomeVal))
                        {
                            ++j3;
                        }

                        if (compareBiomesById(i3, biomeVal))
                        {
                            ++j3;
                        }

                        if (j3 >= 3)
                        {
                            aint2[j1 + i1 * par3] = i2;
                        }
                        else
                        {
                            aint2[j1 + i1 * par3] = biomeVal;
                        }
                    }
                }
                // not the GenLayerHills stuff is done so run BoP subbiome replacements if it's on
                if (this.BoPSubBiomeReplacer!= null) {
                    this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                    int old = aint2[j1 + i1 * par3];
                    aint2[j1 + i1 * par3] = BoPSubBiomeReplacer.replacement(
                            aint2[j1 + i1 * par3], randomCallback,j1 + par1,i1 + par2);
                    if (aint2[j1 + i1 * par3]!= old) {
                        logger.info("BoP subbiome :"+old + " to "+aint2[j1 + i1 * par3]);
                    }
                }
            }
        }

        return aint2;
    }
}