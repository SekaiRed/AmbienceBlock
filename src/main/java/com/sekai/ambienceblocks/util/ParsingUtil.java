package com.sekai.ambienceblocks.util;

import com.sekai.ambienceblocks.ambience.util.AmbienceEnumName;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import com.google.common.base.Predicate;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ParsingUtil {
    //this used to be much worse, hell yeah
    public static <E extends Enum<E>> String getCachedEnumName(E value) {
        if(value instanceof AmbienceEnumName)
            return ((AmbienceEnumName) value).getName();
        else
            return value.name();
    }

    public static <E extends Enum<E>> E tryParseEnum(String value, E defaultVal) {
        try {
            return E.valueOf(defaultVal.getDeclaringClass(), value);
        } catch (IllegalArgumentException e) {
            return defaultVal;
        }
    }

    public static int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static int tryParseInt(String value) {
        return tryParseInt(value, 0);
    }

    public static long tryParseLong(String value, long defaultVal) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static long tryParseLong(String value) {
        return tryParseLong(value, 0);
    }

    public static float tryParseFloat(String value, float defaultVal) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static float tryParseFloat(String value) {
        return tryParseFloat(value, 0f);
    }

    public static double tryParseDouble(String value, double defaultVal) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static double tryParseDouble(String value) {
        return tryParseDouble(value, 0D);
    }

    public static boolean isNumericWithDecimal(String str) {
        if ( str == null )
        {
            return false;
        }
        int strSize = str.length();
        for (int i = 0; i < strSize; i++)
        {
            if (!Character.isDigit(str.charAt(i)) || str.charAt(i) != 46)
                return false;
        }
        return true;
    }

    public static int nthLastIndexOf(int nth, String ch, String string) {
        if (nth <= 0) return string.length();
        return nthLastIndexOf(--nth, ch, string.substring(0, string.lastIndexOf(ch)));
    }

    public static boolean isNumberOrDot(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

    public static boolean isNumber(char c) {
        return (c >= '0' && c <= '9');
    }

    public static int countChar(String s, char c) {
        int count = 0;
        int strSize = s.length();
        for (int i = 0; i < strSize; i++)
        {
            if(s.charAt(i) == c)
                count++;
        }
        return count;
    }

    public static Vector3d blockPosToVec3d(BlockPos pos) {
        return new Vector3d(pos.getX(), pos.getY(), pos.getZ());
    }

    public static String customBlockPosToString(BlockPos pos) {
        return "(" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")";
    }

    public static Vector3d entityVectorPos(Entity entity) {
        return new Vector3d(entity.posX, entity.posY, entity.posZ);
    }

    //Predicate
    public static final Predicate<String> numberFilter = (stringIn) -> {
        if (!net.minecraft.util.StringUtils.isNullOrEmpty(stringIn)) {
            int strSize = stringIn.length();
            for (int i = 0; i < strSize; i++) {
                if (!ParsingUtil.isNumber(stringIn.charAt(i)))
                    return false;
            }

        }
        return true;
    };

    public static final Predicate<String> negativeNumberFilter = (stringIn) -> {
        if (net.minecraft.util.StringUtils.isNullOrEmpty(stringIn)) {
            return true;
        } else {
            int strSize = stringIn.length();
            for (int i = 0; i < strSize; i++)
            {
                if (ParsingUtil.isNumber(stringIn.charAt(i)) || (i == 0 && stringIn.charAt(i) == '-'))
                    continue;

                return false;
            }

            return true;
        }
    };

    public static final Predicate<String> decimalNumberFilter = (stringIn) -> {
        if (net.minecraft.util.StringUtils.isNullOrEmpty(stringIn)) {
            return true;
        } else {
            int strSize = stringIn.length();
            for (int i = 0; i < strSize; i++)
            {
                if (!ParsingUtil.isNumberOrDot(stringIn.charAt(i)))
                    return false;
            }

            if(ParsingUtil.countChar(stringIn, '.') > 1)
                return false;

            return true;
        }
    };

    public static final Predicate<String> negativeDecimalNumberFilter = (stringIn) -> {
        if (net.minecraft.util.StringUtils.isNullOrEmpty(stringIn)) {
            return true;
        } else {
            int strSize = stringIn.length();
            for (int i = 0; i < strSize; i++)
            {
                if (ParsingUtil.isNumberOrDot(stringIn.charAt(i)) || (i == 0 && stringIn.charAt(i) == '-'))
                    continue;

                return false;
            }

            if(ParsingUtil.countChar(stringIn, '.') > 1)
                return false;

            return true;
        }
    };

    /*//Length of 0, always returns true
        if(compared.length() == 0)
            return true;

        if(compared.charAt(0) == '#') {
            //Starts with '#' which means this is an absolute comparision
            return original.equals(compared.substring(1));
        } else {
            //No special case, check if original contains compared
            return original.contains(compared);
        }*/

    /*public static boolean validateString(String original, String compared) {

        String[] split = compared.split("/");

        for (String s : split) {
            //System.out.println(s + " for " + compared + " vs " + original);
            //Length of 0, always returns true
            if(s.length() == 0)
                continue;

            if(s.charAt(0) == '#') {
                //Starts with '#' which means this is an absolute comparision

                //If it follows up with a - sign then invert the output
                if(s.charAt(1) != '-') {
                    if(original.equals(s.substring(1)))
                        continue;
                } else {
                    if(!original.equals(s.substring(2)))
                        continue;
                }
            } else {
                //No special case, check if original contains compared

                //If it follows up with a - sign then invert the output
                if(s.charAt(0) != '-') {
                    if(original.contains(s))
                        continue;
                } else {
                    if(!original.contains(s.substring(1)))
                        continue;
                }
            }
            //System.out.println("false is " + s + " for " + compared + " vs " + original);
            return false;
        }
        //System.out.println("true is " + compared + " vs " + original);
        return true;
    }*/

    public static boolean validateString(String original, String compared) {
        if(compared.isEmpty())
            return true;

        String[] ors = compared.split("!");
        labelOr:for (String or : ors) {
            //boolean isTrue = false;
            String[] ands = or.split("/");
            for (String and : ands) {
                //Atleast one and returned false, go to the next or iteration
                if(!validateSingleString(original, and))
                    continue labelOr;
            }
            //all and conditions returned true, let's go
            return true;
            //if(isTrue)
            //    return true;
        }

        return false;
    }

    public static boolean validateSingleString(String original, String s) {
        if(s.charAt(0) == '#') {
            //Starts with '#' which means this is an absolute comparision

            //If it follows up with a - sign then invert the output
            if(s.charAt(1) != '-') {
                return original.equals(s.substring(1));
            } else {
                return !original.equals(s.substring(2));
            }
        } else {
            //No special case, check if original contains compared

            //If it follows up with a - sign then invert the output
            if(s.charAt(0) != '-') {
                return original.contains(s);
            } else {
                return !original.contains(s.substring(1));
            }
        }
    }

    public static String getEntityRegistryName(Entity entity) {
        return EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString();
    }

    /*public static Vector3i Vec3dToVec3i(Vector3d offset) {
        return new Vector3i(Math.round(offset.x), Math.round(offset.y), Math.round(offset.z));
    }*/

    /*todo moved to AmbienceController as a static method, not sure if it works yet
    public static boolean isValidSound(String name) {
        for(ResourceLocation resource : Minecraft.getInstance().getSoundHandler().getAvailableSounds()) {
            if(resource.toString().equals(name))
                return true;
        }
        return false;
    }*/
}
