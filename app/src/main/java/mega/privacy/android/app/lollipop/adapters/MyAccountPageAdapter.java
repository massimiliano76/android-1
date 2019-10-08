package mega.privacy.android.app.lollipop.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import mega.privacy.android.app.MegaApplication;
import mega.privacy.android.app.R;
import mega.privacy.android.app.lollipop.managerSections.MyAccountFragmentLollipop;
import mega.privacy.android.app.lollipop.managerSections.MyStorageFragmentLollipop;
import nz.mega.sdk.MegaApiAndroid;

import static mega.privacy.android.app.utils.LogUtil.*;

public class MyAccountPageAdapter extends FragmentPagerAdapter {

    private static final int PAGE_COUNT = 2;
    private Context context;
    private MegaApiAndroid megaApi;

    public MyAccountPageAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;

        if (megaApi == null){
            megaApi = MegaApplication.getInstance().getMegaApi();
        }
    }

    @Override
    public Fragment getItem(int position) {
        logDebug("Position: " + position);
        switch (position){
            case 0: {
                return MyAccountFragmentLollipop.newInstance();
            }
            case 1:{
                return MyStorageFragmentLollipop.newInstance();
            }
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position){
            case 0: {
                return context.getString(R.string.tab_my_account_general).toLowerCase();
            }
            case 1:{
                if (megaApi.isBusinessAccount()) {
                    return context.getString(R.string.tab_my_account_usage).toLowerCase();
                }

                return context.getString(R.string.tab_my_account_storage).toLowerCase();
            }
        }
        return null;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }
}
