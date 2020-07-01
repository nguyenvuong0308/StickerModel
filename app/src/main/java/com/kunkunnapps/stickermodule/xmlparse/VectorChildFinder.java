package com.kunkunnapps.stickermodule.xmlparse;

import android.content.Context;

/**
 * Created by ${Deven} on 2/1/18.
 * PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(Color.BLUE, PorterDuff.Mode.SRC_ATOP);
 * myImageView.setColorFilter(porterDuffColorFilter);
 */

public class VectorChildFinder {

    public VectorDrawableCompat vectorDrawable;

    /**
     * @param context Your Activity Context
     * @param vectorRes Path of your vector drawable resource
     */
    public VectorChildFinder(Context context, int vectorRes) {
        vectorDrawable = VectorDrawableCompat.create(context.getResources(),
                vectorRes, null);
        vectorDrawable.setAllowCaching(false);
    }


    /**
     * @param pathName Path name that you gave in vector drawable file
     * @return A Object type of VectorDrawableCompat.VFullPath
     */
    public VectorDrawableCompat.VFullPath findPathByName(String pathName) {
        return (VectorDrawableCompat.VFullPath) vectorDrawable.getTargetByName(pathName);
    }

    /**
     * @param groupName Group name that you gave in vector drawable file
     * @return A Object type of VectorDrawableCompat.VGroup
     */
    public VectorDrawableCompat.VGroup findGroupByName(String groupName) {
        return (VectorDrawableCompat.VGroup) vectorDrawable.getTargetByName(groupName);
    }

}
