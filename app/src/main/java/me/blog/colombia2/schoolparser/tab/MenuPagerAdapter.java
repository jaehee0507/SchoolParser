package me.blog.colombia2.schoolparser.tab;

import android.support.v4.app.*;
import java.util.*;

public class MenuPagerAdapter extends FragmentPagerAdapter {
    protected ArrayList<Fragment> fragmentList;
    protected ArrayList<String> fragmentTitleNames;

    public MenuPagerAdapter(FragmentManager fm) {
        super(fm);
        fragmentList = new ArrayList<>();
        fragmentTitleNames = new ArrayList<>();
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleNames.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragmentTitleNames.get(position);
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof Fragment)
            return fragmentList.indexOf((Fragment) object);
        
        return super.getItemPosition(object);
    }
}
