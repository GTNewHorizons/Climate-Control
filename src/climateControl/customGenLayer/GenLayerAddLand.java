
package climateControl.customGenLayer;
import climateControl.utils.IntPad;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

/**
 * This is GenLayerAddIsland except that it doesn't automatically extend frozen
 * zones into oceans. Oh, and it has a better name since the original doesn't actually
 * add any islands, it add *to* existing land.
 * @author Zeno410
 */
public class GenLayerAddLand extends GenLayerNeighborTesting
{
    private static final String __OBFID = "CL_00000551";
    private final boolean separate;
    private final GenLayer realParent;
    private IntPad output = new IntPad();


    public GenLayerAddLand(long par1, GenLayer par3GenLayer,boolean separate)
    {
        super(par1);
        this.parent = par3GenLayer;
        realParent = par3GenLayer;
        this.separate = separate;
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
        int[] aint1 = output.pad(par3*par4);
        poison(aint1,par3*par4);
        try {
        taste(aint,k1*l1);
        } catch (Exception e) {
            throw new RuntimeException(realParent.toString());
        }

        for (int i2 = 0; i2 < par4 ; i2++)
        {
            for (int j2 = 0; j2 < par3 ; j2++)
            {
                int k2 = aint[j2 + 0 + (i2 + 0) * k1];
                int l2 = aint[j2 + 2 + (i2 + 0) * k1];
                int i3 = aint[j2 + 0 + (i2 + 2) * k1];
                int j3 = aint[j2 + 2 + (i2 + 2) * k1];
                int k3 = aint[j2 + 1 + (i2 + 1) * k1];
                this.initChunkSeed((long)(j2 + par1), (long)(i2 + par2));

                if (isOceanic(k3) && (!isOceanic(k2) || !isOceanic(l2) || !isOceanic(i3) || !isOceanic(j3)))
                {
                    int l3 = 1;
                    int i4 = 0;

                    if (!isOceanic(k2) && this.nextInt(l3++) == 0)
                    {
                        if ((!separate)||this.acceptableNeighbors(k2, aint, i2, j2, k1))
                            i4 = k2;
                    }

                    if (!isOceanic(l2) && this.nextInt(l3++) == 0)
                    {
                        if ((!separate)||this.acceptableNeighbors(l2, aint, i2, j2, k1))
                             i4 = l2;
                    }

                    if (!isOceanic(i3) && this.nextInt(l3++) == 0)
                    {
                        if ((!separate)||this.acceptableNeighbors(i3, aint, i2, j2, k1))
                            i4 = i3;
                    }

                    if (!isOceanic(j3) && this.nextInt(l3++) == 0)
                    {
                        if ((!separate)||this.acceptableNeighbors(j3, aint, i2, j2, k1))
                            i4 = j3;
                    }

                    if (this.nextInt(3) == 0)
                    {
                        aint1[j2 + (i2) * par3] = i4;
                    }
                    else {
                        aint1[j2 + (i2) * par3] = k3;
                    }
                }
                else if (!isOceanic(k3) && (isOceanic(k2) || isOceanic(l2) || isOceanic(i3) || isOceanic(j3)))
                {
                    if (this.nextInt(5) == 0)
                    {
                        if (isOceanic(k2)) aint1[j2 + i2 * par3] = k2;
                        if (isOceanic(l2)) aint1[j2 + i2 * par3] = l2;
                        if (isOceanic(i3)) aint1[j2 + i2 * par3] = i3;
                        if (isOceanic(j3)) aint1[j2 + i2 * par3] = j3;
                    }
                    else
                    {
                        aint1[j2 + i2 * par3] = k3;
                    }
                }
                else
                {
                    aint1[j2 + i2 * par3] = k3;
                }

                if (aint1[j2 + i2 * par3]<0) throw new RuntimeException("i2 "+i2 + " j2 "+j2 + " k2 " + k2 + " l2 " + l2 +
                        " i3 " + i3 + " j3 " + j3 + " k3 " + k3 + " orig "+ aint[j2 + 1 + (i2 + 1) * k1]);
            }
        }
        taste(aint1,par3*par4);
        return aint1;
    }

}