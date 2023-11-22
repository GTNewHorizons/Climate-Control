package climatecontrol.generator;

import java.io.File;
import java.io.IOException;

import net.minecraft.world.WorldType;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.GenLayerRiver;
import net.minecraft.world.gen.layer.GenLayerRiverMix;
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom;

import climatecontrol.ClimateChooser;
import climatecontrol.api.ClimateControlSettings;
import climatecontrol.api.IslandClimateMaker;
import climatecontrol.customGenLayer.ConfirmBiome;
import climatecontrol.customGenLayer.GenLayerAddBiome;
import climatecontrol.customGenLayer.GenLayerAddLand;
import climatecontrol.customGenLayer.GenLayerAdjustIsland;
import climatecontrol.customGenLayer.GenLayerBandedClimate;
import climatecontrol.customGenLayer.GenLayerBiomeByTaggedClimate;
import climatecontrol.customGenLayer.GenLayerBreakMergers;
import climatecontrol.customGenLayer.GenLayerConfirm;
import climatecontrol.customGenLayer.GenLayerConstant;
import climatecontrol.customGenLayer.GenLayerContinentalShelf;
import climatecontrol.customGenLayer.GenLayerForceStartLand;
import climatecontrol.customGenLayer.GenLayerIdentifiedClimate;
import climatecontrol.customGenLayer.GenLayerLandReport;
import climatecontrol.customGenLayer.GenLayerLessRiver;
import climatecontrol.customGenLayer.GenLayerLimitedCache;
import climatecontrol.customGenLayer.GenLayerLowlandRiverMix;
import climatecontrol.customGenLayer.GenLayerOceanicIslands;
import climatecontrol.customGenLayer.GenLayerOceanicMushroomIsland;
import climatecontrol.customGenLayer.GenLayerPrettyShore;
import climatecontrol.customGenLayer.GenLayerRandomBiomes;
import climatecontrol.customGenLayer.GenLayerSmoothClimate;
import climatecontrol.customGenLayer.GenLayerSmoothCoast;
import climatecontrol.customGenLayer.GenLayerSubBiome;
import climatecontrol.customGenLayer.GenLayerWidenRiver;
import climatecontrol.customGenLayer.GenLayerZoomBiome;
import climatecontrol.genLayerPack.GenLayerAddIsland;
import climatecontrol.genLayerPack.GenLayerFuzzyZoom;
import climatecontrol.genLayerPack.GenLayerPack;
import climatecontrol.genLayerPack.GenLayerRareBiome;
import climatecontrol.genLayerPack.GenLayerSmooth;
import climatecontrol.genLayerPack.GenLayerZoom;
import climatecontrol.utils.IntRandomizer;
import climatecontrol.utils.RandomIntUser;
import climatecontrol.utils.StringWriter;

/**
 * This class creates a world generator from a ClimateControlSettings and a world seed
 * 
 * @author Zeno410
 */
public class CorrectedContinentsGenerator extends AbstractWorldGenerator {

    private File serverDirectoryFile;

    public CorrectedContinentsGenerator(ClimateControlSettings settings, File serverDirectory) {
        super(settings);
        serverDirectoryFile = serverDirectory;
    }

    protected IslandClimateMaker islandClimates() {
        return new IslandClimateMaker() {

            RandomIntUser climateChooser = new ClimateChooser(
                settings().hotIncidence.value(),
                settings().warmIncidence.value(),
                settings().coolIncidence.value(),
                settings().snowyIncidence.value());

            public int climate(int x, int z, IntRandomizer randomizer) {
                return climateChooser.value(randomizer);
            }
        };
    }

    protected RandomIntUser landmassIdentifier() {
        if (!this.settings().separateLandmasses.value()) return this.justLand();
        return new RandomIntUser() {

            @Override
            public int value(IntRandomizer randomizer) {
                return randomizer.nextInt(10000) + 256;
            }

        };
    }

    protected void setOceanSubBiomes() {

    }

    public GenLayerRiverMix fromSeed(long worldSeed, WorldType worldType) {

        this.subBiomeChooser.clear();
        this.subBiomeChooser.set(settings().biomeSettings());
        setOceanSubBiomes();
        this.mBiomeChooser.set(settings().biomeSettings());
        setRules();
        boolean climatesAssigned = false;

        GenLayer emptyOcean = new GenLayerConstant(0);
        GenLayerPack genlayerisland = new GenLayerOceanicIslands(
            1L,
            emptyOcean,
            settings().largeContinentFrequency.value(),
            this.landmassIdentifier(),
            settings().separateLandmasses.value(),
            "Large Continent");
        GenLayerPack genlayeraddisland = growRound(genlayerisland, 2L, 3L, climatesAssigned);

        genlayeraddisland = new GenLayerFuzzyZoom(2000L, genlayeraddisland);
        genlayeraddisland = new GenLayerSmooth(2004L, genlayeraddisland);
        GenLayerPack mediumContinents = new GenLayerOceanicIslands(
            4L,
            genlayeraddisland,
            settings().mediumContinentFrequency.value(),
            this.landmassIdentifier(),
            settings().separateLandmasses.value(),
            "Medium Continent");

        genlayeraddisland = growRound(mediumContinents, 5L, 7L, climatesAssigned);
        if (settings().separateLandmasses.value()) {
            genlayeraddisland = new GenLayerBreakMergers(5L + 1000, genlayeraddisland);
        }
        GenLayer genlayerzoom = new GenLayerFuzzyZoom(2001L, genlayeraddisland);
        genlayerzoom = new GenLayerSmooth(2008l, genlayerzoom);
        if (settings().separateLandmasses.value()) {
            genlayeraddisland = new GenLayerBreakMergers(5L + 1000, genlayeraddisland);
        }
        GenLayerPack smallContinents = new GenLayerOceanicIslands(
            8L,
            genlayerzoom,
            settings().smallContinentFrequency.value(),
            this.landmassIdentifier(),
            settings().separateLandmasses.value(),
            "Small Continent");
        if (settings().forceStartContinent.value()) {
            smallContinents = new GenLayerForceStartLand(smallContinents);
        }
        genlayeraddisland = growRound(smallContinents, 2L, 3L, climatesAssigned);
        if (settings().doFull()) {
            // genlayeraddisland = new GenLayerDefineClimate(10L, genlayeraddisland,settings());
            // genlayeraddisland = new GenLayerSmoothClimate(1010L,genlayeraddisland);
            genlayeraddisland = climateLayer(1014L, genlayeraddisland, settings());
            climatesAssigned = true;
        }

        if (settings().testingMode.value()) {
            genlayeraddisland = new GenLayerConfirm(genlayeraddisland);
            // genlayeraddisland = this.reportOn(genlayeraddisland, "smoothed.txt");
        }

        genlayeraddisland = new GenLayerZoom(2002L, genlayeraddisland);
        genlayeraddisland = new GenLayerSmooth(2012L, genlayeraddisland);
        if (settings().separateLandmasses.value()) {
            genlayeraddisland = new GenLayerBreakMergers(2012L + 1000, genlayeraddisland);
        }
        if (climatesAssigned) {
            // climates are already defined so the island creator has to use a climate definer;
            genlayeraddisland = new GenLayerOceanicIslands(
                11L,
                genlayeraddisland,
                settings().largeIslandFrequency.value(),
                // if separating have to use identifiedClimates();
                islandClimates(1),
                settings().separateLandmasses.value(),
                "Large Island");
        } else {
            genlayeraddisland = new GenLayerOceanicIslands(
                11L,
                genlayeraddisland,
                settings().largeIslandFrequency.value(),
                this.landmassIdentifier(),
                settings().separateLandmasses.value(),
                "Large Island");
        }
        // add land without merging if separating and just add otherwise
        genlayeraddisland = growRound(genlayeraddisland, 13L, 15L, climatesAssigned);
        if (settings().separateLandmasses.value()) {
            genlayeraddisland = new GenLayerBreakMergers(13L + 1000, genlayeraddisland);
        }
        if (settings().testingMode.value()) {
            genlayeraddisland = this.reportOn(genlayeraddisland, "largeIslands.txt");
        }
        if (settings().doHalf()) {
            // genlayeraddisland = new GenLayerDefineClimate(14L, genlayeraddisland,settings());
            // genlayeraddisland = new GenLayerSmoothClimate(1014L,genlayeraddisland);
            genlayeraddisland = climateLayer(1014L, genlayeraddisland, settings());
            climatesAssigned = true;
        }
        if (settings().testingMode.value()) {
            genlayeraddisland = new GenLayerConfirm(genlayeraddisland);
            // genlayeraddisland = this.reportOn(genlayeraddisland, "smoothed.txt");
        }
        genlayeraddisland = new GenLayerZoom(2003L, genlayeraddisland);
        genlayeraddisland = new GenLayerSmooth(2017L, genlayeraddisland);
        if (settings().separateLandmasses.value()) {
            genlayeraddisland = new GenLayerBreakMergers(2017L + 1000, genlayeraddisland);
        }
        if (climatesAssigned) {
            // climates are already defined so the island creator has to use a climate definer;
            genlayeraddisland = new GenLayerOceanicIslands(
                17L,
                genlayeraddisland,
                settings().mediumIslandFrequency.value(),
                // if separating have to use identifiedClimates();
                islandClimates(0),
                settings().separateLandmasses.value(),
                "Medium Island");
        } else {
            genlayeraddisland = new GenLayerOceanicIslands(
                17L,
                genlayeraddisland,
                settings().mediumIslandFrequency.value(),
                this.landmassIdentifier(),
                settings().separateLandmasses.value(),
                "Medium Island");
        }
        // genlayeraddisland = new GenLayerAddLand(19L, genlayeraddisland);
        genlayeraddisland = new GenLayerAdjustIsland(
            21L,
            genlayeraddisland,
            1,
            12,
            12,
            settings().separateLandmasses.value() || climatesAssigned);
        if (settings().quarterSize.value()) {
            // genlayeraddisland = new GenLayerDefineClimate(20L, genlayeraddisland,settings());
            // genlayeraddisland = new GenLayerSmoothClimate(1014L,genlayeraddisland);
            genlayeraddisland = climateLayer(1014L, genlayeraddisland, settings());
            climatesAssigned = true;
        }
        genlayeraddisland = new GenLayerSmoothClimate(22L, genlayeraddisland);
        if (settings().testingMode.value()) {
            genlayeraddisland = this.reportOn(genlayeraddisland, "mediumIslands.txt");
        }
        if (settings().testingMode.value()) {
            genlayeraddisland = new GenLayerConfirm(genlayeraddisland);
            // genlayeraddisland = this.reportOn(genlayeraddisland, "smoothed.txt");
        }
        genlayeraddisland = new GenLayerLimitedCache(genlayeraddisland, 100);
        // genlayeraddisland = new GenLayerTestClimateSmooth(genlayeraddisland);
        GenLayer genlayerdeepocean = new GenLayerContinentalShelf(23L, genlayeraddisland);
        GenLayer genlayeraddmushroomisland = new GenLayerOceanicMushroomIsland(
            24L,
            genlayerdeepocean,
            settings().mushroomIslandIncidence.value());

        GenLayer genlayer3 = GenLayerZoom.magnify(1002L, genlayeraddmushroomisland, 0);
        genlayer3.initWorldGenSeed(worldSeed);
        if (settings().testingMode.value()) {
            genlayeraddisland = this.reportOn(genlayeraddisland, "preBiome.txt");
        }
        if (settings().smootherCoasts.value()) {
            return climateControlExpansion(worldSeed, worldType, genlayer3, settings());
        }
        return this.vanillaExpansion(worldSeed, worldType, genlayer3, settings());
    }

    private GenLayerPack growRound(GenLayerPack genlayeraddisland, long firstSeed, long secondSeed,
        boolean climatesAssigned) {
        // add land without merging if separating and just add otherwise
        if (settings().separateLandmasses.value() || climatesAssigned) {
            genlayeraddisland = new GenLayerAddLand(firstSeed, genlayeraddisland, true);
            genlayeraddisland = new GenLayerBreakMergers(firstSeed + 1000, genlayeraddisland);
            genlayeraddisland = new GenLayerAdjustIsland(secondSeed, genlayeraddisland, 3, 11, 12, true);
            genlayeraddisland = new GenLayerBreakMergers(secondSeed + 1000, genlayeraddisland);
        } else {
            genlayeraddisland = new GenLayerAddLand(firstSeed, genlayeraddisland, climatesAssigned);
            genlayeraddisland = new GenLayerAdjustIsland(secondSeed, genlayeraddisland, 3, 11, 12, climatesAssigned);
        }
        return genlayeraddisland;
    }

    private GenLayerPack separatedGrowth(GenLayerPack genlayeraddisland, long secondSeed, boolean climatesAssigned) {
        // add land without merging if separating and just add otherwise
        genlayeraddisland = new GenLayerAddLand(secondSeed, genlayeraddisland, true);
        genlayeraddisland = new GenLayerBreakMergers(secondSeed + 1000, genlayeraddisland);
        genlayeraddisland = new GenLayerAdjustIsland(secondSeed + 2000, genlayeraddisland, 3, 11, 12, true);
        genlayeraddisland = new GenLayerBreakMergers(secondSeed + 3000, genlayeraddisland);
        return genlayeraddisland;
    }

    private IslandClimateMaker islandClimates(int level) {
        if (this.settings().bandedClimateWidth.value() > 0) {
            int multiplier = 1;
            if (settings().doFull()) {
                if (level == 0) multiplier = 4;
                if (level == 1) multiplier = 2;
            }
            if (settings().doHalf()) {
                if (level == 0) multiplier = 2;
            }
            return new GenLayerBandedClimate(0, null, settings(), multiplier);
        }
        // normal system
        return settings().separateLandmasses.value() ? this.identifiedClimate() : this.islandClimates();
    }

    GenLayerPack reportOn(GenLayerPack reportedOn, String fileName) {
        if (this.serverDirectoryFile != null) {
            try {
                StringWriter target = new StringWriter(new File(serverDirectoryFile, fileName));
                reportedOn = new GenLayerLandReport(reportedOn, 40, target);
                return reportedOn;
            } catch (IOException iOException) {
                throw new RuntimeException(iOException);
            }
        }
        return reportedOn;
    }

    private GenLayerPack climateLayer(long seed, GenLayer parent, ClimateControlSettings settings) {
        if (settings.bandedClimateWidth.value() > 0) {
            return new GenLayerBandedClimate(seed, parent, settings, 1);
        }
        return new GenLayerIdentifiedClimate(seed, parent, settings());
    }

    private RandomIntUser justLand() {
        return new RandomIntUser() {

            @Override
            public int value(IntRandomizer randomizer) {
                return 1;
            }

        };

    }

    private IslandClimateMaker identifiedClimate() {
        return new IslandClimateMaker() {

            IslandClimateMaker island = islandClimates();
            RandomIntUser identifier = landmassIdentifier();

            @Override
            public int climate(int x, int z, IntRandomizer randomizer) {
                return island.climate(x, z, randomizer) + 4 * identifier.value(randomizer);
            }
        };
    }

    public GenLayerRiverMix climateControlExpansion(long par0, WorldType par2WorldType, GenLayer genlayer3,
        ClimateControlSettings settings) {
        byte b0 = 4;

        if (par2WorldType == WorldType.LARGE_BIOMES) {
            b0 = 6;
        } else b0 = settings.biomeSize.value()
            .byteValue();

        GenLayer genlayer = GenLayerZoom.magnify(1003L, genlayer3, 0);
        GenLayer genlayerriverinit = new GenLayerLessRiver(
            102L,
            genlayer,
            rtgAwareRiverReduction(settings().percentageRiverReduction.value(), par2WorldType));
        GenLayer subBiomeFlags = new GenLayerLessRiver(102L, genlayer, 0);
        GenLayerPack biomes = null;
        if (settings.randomBiomes.value()) {
            biomes = new GenLayerRandomBiomes(par0, genlayer3, settings);
        } else {
            biomes = new GenLayerBiomeByTaggedClimate(par0, genlayer3, settings);
        }
        if (settings().testingMode.value()) {
            biomes = this.reportOn(biomes, "Biomes.txt");
        }
        GenLayer object = new GenLayerZoom(1004L, biomes);
        object = new GenLayerAddBiome(1005L, object);
        object = new GenLayerSmooth(103L, object);
        object = new GenLayerZoomBiome(1006L, object);
        object = new GenLayerAddBiome(1007L, object);
        object = new GenLayerSmoothCoast(104L, object);
        subBiomeFlags = GenLayerZoom.magnify(1008L, subBiomeFlags, 2);
        GenLayerPack genlayerhills = null;
        genlayerhills = new GenLayerSubBiome(
            1009L,
            object,
            subBiomeFlags,
            subBiomeChooser,
            mBiomeChooser,
            settings().doBoPSubBiomes());
        genlayer = GenLayerZoom.magnify(1010L, genlayerriverinit, 2);
        genlayer = GenLayerZoom.magnify(1010L, genlayer, b0);
        GenLayer genlayerriver = new GenLayerRiver(1L, genlayer);
        if (settings.widerRivers.value()) {
            genlayerriver = new GenLayerWidenRiver(1L, genlayerriver);
        }
        GenLayer genlayersmooth = new GenLayerSmoothCoast(1000L, genlayerriver);
        object = new GenLayerRareBiome(1001L, genlayerhills);
        // object = new ConfirmBiome(object);

        for (int j = 0; j < b0; ++j) {
            object = new GenLayerZoom((long) (1000 + j), (GenLayer) object, true);
            // object = new ConfirmBiome(object);

            if (j == 0) {
                object = new GenLayerAddIsland(3L, (GenLayer) object);
                object = new GenLayerSmoothCoast(100L, object);
            }

            if (j == 1) {
                object = new GenLayerSmoothCoast(100L, object);
            }
            if (settings.wideBeaches.value()) {
                if (j == 0) {
                    object = new GenLayerPrettyShore(1000L, (GenLayer) object, 1.0F, rules());
                }
            } else {
                if (j == 1) {
                    object = new GenLayerPrettyShore(1000L, (GenLayer) object, 1.0F, rules());
                }
            }
        }

        GenLayer genlayersmooth1 = new GenLayerSmooth(1000L, (GenLayer) object);
        genlayersmooth1 = new ConfirmBiome(genlayersmooth1);
        if (settings().cachingOn()) {
            // genlayersmooth1 = new GenLayerLimitedCache(genlayersmooth1,16*settings().cacheSize());
        }
        GenLayerRiverMix genlayerrivermix = new GenLayerLowlandRiverMix(
            100L,
            genlayersmooth1,
            genlayersmooth,
            (settings().maxRiverChasm.value()
                .floatValue()),
            rules());

        GenLayerVoronoiZoom genlayervoronoizoom = new GenLayerVoronoiZoom(10L, genlayerrivermix);
        genlayerrivermix.initWorldGenSeed(par0);
        genlayervoronoizoom.initWorldGenSeed(par0);

        return genlayerrivermix;
    }

}
