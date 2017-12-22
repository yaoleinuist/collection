package com.lzhsite.core.ensure;

import java.util.Collection;

import com.lzhsite.core.ensure.extensions.EnsureParamBooleanExtension;
import com.lzhsite.core.ensure.extensions.EnsureParamCollectionExtension;
import com.lzhsite.core.ensure.extensions.EnsureParamEnumExtension;
import com.lzhsite.core.ensure.extensions.EnsureParamNumberExtension;
import com.lzhsite.core.ensure.extensions.EnsureParamObjectExtension;
import com.lzhsite.core.ensure.extensions.EnsureParamStringExtension;

/**
 * Created by Jintao on 2015/6/8.
 */
public class Ensure {

    public static EnsureParamObjectExtension that(Object tObject){
        return new EnsureParamObjectExtension(tObject);
    }

    public static EnsureParamBooleanExtension that(boolean tObject){
        return new EnsureParamBooleanExtension(tObject);
    }

    public static <TObject extends Collection> EnsureParamCollectionExtension that(TObject tObject){
        return new EnsureParamCollectionExtension(tObject);
    }

    public static <TObject extends Boolean> EnsureParamBooleanExtension that(TObject tObject){
        return new EnsureParamBooleanExtension(tObject);
    }

    public static <TObject extends Number> EnsureParamNumberExtension that (TObject tObject){
        return new EnsureParamNumberExtension(tObject);
    }

    public static <TObject extends Enum> EnsureParamEnumExtension that (TObject tObject){
        return new EnsureParamEnumExtension(tObject);
    }

    public static EnsureParamStringExtension that (String tObject){
        return new EnsureParamStringExtension(tObject);
    }

}
