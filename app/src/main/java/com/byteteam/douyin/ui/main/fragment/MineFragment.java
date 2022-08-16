package com.byteteam.douyin.ui.main.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.byteteam.douyin.R;
import com.byteteam.douyin.databinding.FragmentMineBinding;
import com.byteteam.douyin.logic.database.model.User;
import com.byteteam.douyin.ui.main.adapter.SectionsPagerAdapter;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation;

/**
 * @introduction： 个人主页Fragment
 * @author： 何文鹏
 * @time： 2022/8/16 21:53
 */
public class MineFragment extends Fragment {

    public static MineFragment newInstance() {
        return new MineFragment();
    }

    private FragmentMineBinding binding;

    private boolean singleLine = true;

    private View.OnClickListener noFunctionListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(getLayoutInflater(),container,false);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(requireActivity().getTitle());

        noFunctionListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoFunction();
            }
        };

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(requireContext(), requireActivity().getSupportFragmentManager());
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);
        ImageButton btn_expand = binding.btnExpand;
        btn_expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TextView tv = binding.tvInfo;
                singleLine = !singleLine;
                tv.setSingleLine(singleLine);
            }
        });

        bindNoFunction();

        setAvatar();

        displayUser(new User().getExampleUser());
        return binding.getRoot();
    }

    private void bindNoFunction() {
        binding.tvEditProfile.setOnClickListener(noFunctionListener);
        binding.tvNewFriends.setOnClickListener(noFunctionListener);
        binding.clStore.setOnClickListener(noFunctionListener);
        binding.clOpen.setOnClickListener(noFunctionListener);
        binding.llRates.setOnClickListener(noFunctionListener);
    }


    private void displayUser(User user) {
        binding.tvValueNickname.setText(user.getNickname());
        binding.tvValueCity.setText(user.getCity());
        binding.tvValueCountry.setText(user.getCountry());
        binding.tvValueGender.setText(user.getGender());
        binding.tvInfo.setText(user.getIntroduction());

        if (user.getAvatar() == null) {
            RequestOptions options = RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(3, Color.WHITE));
            Glide.with(this).load(user.getAvatar()).apply(options).into(binding.imgAvatar);

        }

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_scrolling, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setAvatar(){
        RequestOptions options = RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(3, Color.WHITE));
        Glide.with(this).load(R.drawable.wall).apply(options).into(binding.imgAvatar);
        RequestOptions options1 = RequestOptions.bitmapTransform(new BlurTransformation(50,1));
        Glide.with(this).load(R.drawable.wall).apply(options1).into(binding.imgWall);
    }

    public void showNoFunction(){
        Toast.makeText(requireContext(),"FUNCTION IS UNDER DEVELOPMENT, COMING SOON...", Toast.LENGTH_SHORT).show();
    }

}