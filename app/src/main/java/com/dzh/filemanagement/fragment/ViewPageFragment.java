package com.dzh.filemanagement.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dzh.filemanagement.R;

import org.lmw.demo.slidingtab.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class ViewPageFragment extends Fragment {

    private FileListPageFragment mFileListPageFragment = null;
    private FavoritePageFragment mFavoritePageFragment = null;

    private ViewPagerAdapter mAdapter = null;
    private ViewPager mViewPager = null;
    public ArrayList<Fragment> mPagerItemList = new ArrayList<Fragment>();

    private View mView = null;
    private PagerSlidingTabStrip mTabs = null;
    public ViewPager.OnPageChangeListener mDelegatePageListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        findViews(inflater);
        initFragment();

        mPagerItemList.add(mFileListPageFragment);
        mPagerItemList.add(mFavoritePageFragment);

        mAdapter = new ViewPagerAdapter(getFragmentManager(), new String[]{"目录", "收藏"});
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mTabs = (PagerSlidingTabStrip) mView.findViewById(R.id.pageTabs);
        mTabs.setViewPager(mViewPager);


        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mFileListPageFragment.startAnim();
                } else if (position == 1) {
                    mFavoritePageFragment.reLoadFavoriteList();
                    mFavoritePageFragment.startListAnim();
                }
                if (mDelegatePageListener != null) {
                    mDelegatePageListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (mDelegatePageListener != null) {
                    mDelegatePageListener.onPageScrolled(arg0, arg1, arg2);
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if (mDelegatePageListener != null) {
                    mDelegatePageListener.onPageScrollStateChanged(arg0);
                }
            }
        });
        return mView;
    }



    public List<Fragment> getPageFragments() {
        return mPagerItemList;
    }

    public FileListPageFragment getFileListPageFragment() {
        return mFileListPageFragment;
    }

    private void findViews(LayoutInflater inflater) {
        mView = inflater.inflate(R.layout.view_pager, null);
        mViewPager = (ViewPager) mView.findViewById(R.id.viewPager);
    }

    private void initFragment() {
        mFileListPageFragment = new FileListPageFragment();
        mFavoritePageFragment = new FavoritePageFragment();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public boolean isFirst() {
        if (mViewPager.getCurrentItem() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEnd() {
        if (mViewPager.getCurrentItem() == mPagerItemList.size() - 1) {
            return true;
        } else {
            return false;
        }
    }

    /* ViewPager适配器 */
    public class ViewPagerAdapter extends FragmentPagerAdapter {
        private String[] mTitiles = null;

        public ViewPagerAdapter(FragmentManager fm, String[] titles) {
            super(fm);
            mTitiles = titles;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitiles[position];
        }

        @Override
        public int getCount() {
            return mTitiles.length;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            if (position < mPagerItemList.size()) {
                fragment = mPagerItemList.get(position);
            } else {
                fragment = mPagerItemList.get(0);
            }
            return fragment;
        }
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.mDelegatePageListener = listener;
    }

    public int getCurrentPageIndex() {
        return mViewPager.getCurrentItem();
    }

    /**
     * 设置显示第几页
     *
     * @param position
     */
    public void setPage(int position) {
        mViewPager.setCurrentItem(position);
    }


}
