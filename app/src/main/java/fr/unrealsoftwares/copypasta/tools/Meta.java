package fr.unrealsoftwares.copypasta.tools;

import android.content.Context;
import android.content.pm.PackageManager;

public class Meta {

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
