package com.byteteam.douyin.ui.main.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.Rotate;
import com.bumptech.glide.request.RequestOptions;
import com.byteteam.douyin.R;
import com.byteteam.douyin.databinding.FragmentMineBinding;
import com.byteteam.douyin.douyinapi.ApiUtil;
import com.byteteam.douyin.logic.dataSource.UserDataSource;
import com.byteteam.douyin.logic.database.dao.UserDao;
import com.byteteam.douyin.logic.database.model.User;
import com.byteteam.douyin.logic.factory.DaoFactory;
import com.byteteam.douyin.logic.factory.RepositoryFactory;
import com.byteteam.douyin.ui.rank.adapter.ViewPagerAdapter;
import com.byteteam.douyin.util.AnimationUtil;
import com.byteteam.douyin.util.UriUtil;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    private View.OnClickListener noFunctionListener, disNoFunctionListener;




    @Override
    public void onStart() {
        super.onStart();
        doSubscribe();
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            this::updateUser);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMineBinding.inflate(getLayoutInflater(), container, false);
        CollapsingToolbarLayout toolBarLayout = binding.toolbarLayout;
        toolBarLayout.setTitle(requireActivity().getTitle());

        noFunctionListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AnimationUtil.doPressAnimation(view);
                showNoFunction();
            }
        };

        disNoFunctionListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoFunction();
            }
        };

        @SuppressLint("CheckResult") View.OnClickListener getUserListener = view -> {
            UserDataSource userDataSource = RepositoryFactory.provideUserDataRepository(getContext());
            userDataSource.queryUser()
                    .doOnComplete(() -> {
                        ApiUtil.sendAuth(getActivity());
                    })
                    .subscribe(user -> {
                        System.out.println("user: " + user);
                        displayUser(user);
                    });


        };

        View.OnClickListener menuClickListener = view -> {
            AnimationUtil.doPressAnimation(view);
            showMenu(binding.btnSettings);
        };

        ImageButton btn_expand = binding.btnExpand;
        btn_expand.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                TextView tv = binding.tvInfo;
                singleLine = !singleLine;
                tv.setSingleLine(singleLine);
                if (singleLine){
                    Glide.with(getContext()).load(R.drawable.arrow).into(binding.btnExpand);
                }else{
                    Glide.with(getContext()).load(R.drawable.arrow).transform(new Rotate(180)).into(binding.btnExpand);
                }
            }
        });

        ImageButton btn_edit = binding.btnModify;
        btn_edit.setOnClickListener(noFunctionListener);

        // 设置滑动布局
        List<Fragment> list = new ArrayList<>();
        list.add(WorksFragment.newInstance());
        list.add(FollowFragment.newInstance());
        list.add(MyFanFragment.newInstance());
        ViewPagerAdapter adapter = new ViewPagerAdapter(list, getParentFragmentManager(), getLifecycle());
        binding.viewPager.setAdapter(adapter);

        binding.tabs.addTab(binding.tabs.newTab().setText("作品"));
        binding.tabs.addTab(binding.tabs.newTab().setText("关注"));
        binding.tabs.addTab(binding.tabs.newTab().setText("粉丝"));

        // 设置tab栏切换viewpager
        binding.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        ImageButton settingsButton = binding.btnSettings;
        settingsButton.setOnClickListener(menuClickListener);

        // 默认设置背景和头像
        setDefaultBg();

        // 没有这个也可以正常使用,但是这个可以使加载更快,不然会看到默认头像
        initUser();

        binding.imgAvatar.setOnClickListener(getUserListener);

        bindNoFunction();

        return binding.getRoot();

    }

    // 从数据库更新user
    @SuppressLint("CheckResult")
    private void updateUser(Uri background) {
        System.out.println("updateUser:" + background);
        UserDataSource userDataSource = RepositoryFactory.provideLocalUserDataRepository(getContext());
        userDataSource.queryUser()
                .doOnComplete(()->{
                    Toast.makeText(requireContext(),"FAILED TO GET USER",Toast.LENGTH_SHORT).show();
                })
                .subscribe(user -> {
                    UserDao userDao = DaoFactory.provideUserDao(getContext());

                    User newUser = new User(user);
                    newUser.setBackground(UriUtil.convertUriToPath(getContext(),background));
                    Log.e("User",user.toString());
                    Log.e("NewUser",newUser.toString());
                    userDao.deleteUser(user);
                    userDao.insertUser(newUser);

                    // todo
                    RequestOptions options1 = RequestOptions.bitmapTransform(new BlurTransformation(50,1));
                    Glide.with(requireContext()).load(UriUtil.convertUriToPath(getContext(),background)).apply(options1).into(binding.imgWall);

                    displayUser(newUser);
                });
    }

    // 从数据库加载user
    @SuppressLint("CheckResult")
    private void initUser() {
        UserDataSource userDataSource = RepositoryFactory.provideLocalUserDataRepository(getContext());
        userDataSource.queryUser()
                .doOnComplete(() -> {
                    Toast.makeText(getContext(), "FAILED TO GET USER", Toast.LENGTH_SHORT);
                })
                .subscribe(user -> {
                    System.out.println("user: " + user);
                    displayUser(user);
                });
    }


    @SuppressLint("CheckResult")
    private void doSubscribe() {
        UserDataSource userDataSource = RepositoryFactory.provideUserDataRepository(getContext());
        userDataSource.queryUser()
                .subscribe(user -> {
                    System.out.println("user: " + user);
                    displayUser(user);
                });
    }

    // 展示menu，点击了右上角图标后
    @SuppressLint("NonConstantResourceId")
    private void showMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.menu_settings_mine, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {

                case R.id.settings:
                    showNoFunction();
                case R.id.change_bg:
                    // 更改背景图
                    if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(getContext(),"permission allowed",Toast.LENGTH_LONG).show();
                        mGetContent.launch("image/*");
                    }else{
                        requestStoragePermission();

                    }
                    return true;

            }

            return true;
        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu popupMenu) {
                AnimationUtil.doPressAnimation(view);
            }
        });

        popupMenu.show();

    }

    private void bindNoFunction() {
        binding.cvEditProfile.setOnClickListener(noFunctionListener);
        binding.cvNewFriends.setOnClickListener(noFunctionListener);
        binding.clStore.setOnClickListener(noFunctionListener);
        binding.clOpen.setOnClickListener(noFunctionListener);
        binding.llRates.setOnClickListener(disNoFunctionListener);
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(getContext(),"permission allowed",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            Toast.makeText(getContext(),"permission denied",Toast.LENGTH_LONG).show();
        }
    }

    // ui中展示user信息
    private void displayUser(User user) {

        RequestOptions options = RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(3, Color.WHITE));


        // 判断user是否为空
        if (user.getOpen_id().equals("")) {
            binding.tvValueNickname.setText(R.string.tap2login);
            Glide.with(this).load(R.drawable.r_key).apply(options).into(binding.imgAvatar);
        } else {
            if (!Objects.equals(user.getCity(), "")) {
                binding.tvValueCity.setText(user.getCity());
            }

            if (!Objects.equals(user.getCountry(), "")) {
                binding.tvValueCountry.setText(user.getCountry());
            }

            if (!Objects.equals(user.getNickname(), "")) {
                binding.tvValueNickname.setText(user.getNickname());
            }


            RequestOptions options1 = RequestOptions.bitmapTransform(new BlurTransformation(50,1));
            if (user.getBackground() != null && !Objects.equals(user.getBackground(), "")) {
                Glide.with(requireContext()).load(user.getBackground()).apply(options1).into(binding.imgWall);
            }else{
//                Glide.with(this).load(R.drawable.wall).apply(options1).into(binding.imgWall);
            }

            binding.tvValueGender.setText(user.getGender());

            if (Objects.equals(user.getAvatar(), "")) {
                Glide.with(this).load(R.drawable.r_key).apply(options).into(binding.imgAvatar);
            } else {
                Glide.with(this).load(user.getAvatar()).apply(options).into(binding.imgAvatar);
            }


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

    public void setDefaultBg() {
        RequestOptions options = RequestOptions.bitmapTransform(new CropCircleWithBorderTransformation(3, Color.WHITE));
        Glide.with(this).load(R.drawable.r_key).apply(options).into(binding.imgAvatar);
    }

    public void showNoFunction() {
        Toast.makeText(requireContext(), "功能正在开发,即将上线...", Toast.LENGTH_SHORT).show();
    }

}
