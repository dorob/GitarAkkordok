package hu.mobwebhf.gitarakkordok;

/**
 * Created by Benjamin on 2018. 02. 15..
 */

public final class ChordImage {
    public static int getChordImage(String chord) {
        switch (chord) {
            case "A":
                return R.drawable.a_major_1;
            case "Am":
                return R.drawable.a_minor_1;
            case "A#":
                return R.drawable.a_sharp_major_1;
            case "A#m":
                return R.drawable.a_sharp_minor_1;
            case "B":
                return R.drawable.b_major_1;
            case "Bm":
                return R.drawable.b_minor_1;
            case "C":
                return R.drawable.c_major_1;
            case "Cm":
                return R.drawable.c_minor_1;
            case "C#":
                return R.drawable.c_sharp_major_1;
            case "C#m":
                return R.drawable.c_sharp_minor_1;
            case "D":
                return R.drawable.d_major_1;
            case "Dm":
                return R.drawable.d_minor_1;
            case "D#":
                return R.drawable.d_sharp_major_1;
            case "D#m":
                return R.drawable.d_sharp_minor_1;
            case "E":
                return R.drawable.e_major_1;
            case "Em":
                return R.drawable.e_minor_1;
            case "F":
                return R.drawable.f_major_1;
            case "Fm":
                return R.drawable.f_minor_1;
            case "F#":
                return R.drawable.f_sharp_major_1;
            case "F#m":
                return R.drawable.f_sharp_minor_1;
            case "G":
                return R.drawable.g_major_1;
            case "Gm":
                return R.drawable.g_minor_1;
            case "G#":
                return R.drawable.g_sharp_major_1;
            case "G#m":
                return R.drawable.g_sharp_minor_1;
        }
        return 0;
    }
}
