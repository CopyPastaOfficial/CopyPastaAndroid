package fr.unrealsoftwares.copypasta.tools;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Static class to get meta elements from the manifest
 */
public class Meta {

    /**
     * @param context Activity context
     * @return  Return "lite" if this is the app with adverts for example (if it is the free app).
     *          Return "pro" if this is the paying app (without adverts for example).
     */
    public static String getVersion(Context context)
    {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("version");

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return "lite";
    }

    /**
     * @param context Activity context
     * @return Returns the advert proportion
     */
    public static int getAdvertProportion(Context context)
    {
        try {
            return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getInt("advert_proportion");

        } catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }

        return 25;
    }


}
