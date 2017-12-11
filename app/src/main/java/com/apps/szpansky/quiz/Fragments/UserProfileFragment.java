package com.apps.szpansky.quiz.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.apps.szpansky.quiz.DialogsFragments.Loading;
import com.apps.szpansky.quiz.R;
import com.apps.szpansky.quiz.SimpleData.QuestionData;
import com.apps.szpansky.quiz.SimpleData.UserData;
import com.apps.szpansky.quiz.Tasks.GetQuestion;
import com.apps.szpansky.quiz.Tasks.RenewUserAnswer;
import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;


public class UserProfileFragment extends Fragment implements RewardedVideoAdListener {

    private static boolean REWARDED = false;

    UserData userData;
    QuestionData questionData;

    GetQuestion getQuestion;

    TextView textUserPoints;
    TextView textUserNextPoints;
    TextView textUserLvl;
    TextView textUserNextLvl;
    TextView textUserName;
    AdView adView;
    ImageView imageView;
    Button fab;
    Button fab2;
    ProgressBar progressBar;
    TextView completeLvl;
    private RewardedVideoAd mAd;
    private RenewUserAnswer mPasswordTask = null;


    public static UserProfileFragment newInstance(UserData userData) {
        UserProfileFragment userProfileFragment = new UserProfileFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("userData", userData);
        userProfileFragment.setArguments(bundle);

        return userProfileFragment;
    }


    private void setRewardedVideo() {
        mAd = MobileAds.getRewardedVideoAdInstance(getActivity());
        mAd.setRewardedVideoAdListener(this);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userData = (UserData) getArguments().getSerializable("userData");

        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        textUserLvl = view.findViewById(R.id.userLvl);
        textUserNextLvl = view.findViewById(R.id.userNextLvl);
        textUserName = view.findViewById(R.id.userName);
        textUserNextPoints = view.findViewById(R.id.userNextPoints);
        textUserPoints = view.findViewById(R.id.userPoints);
        imageView = view.findViewById(R.id.imageView);
        fab = view.findViewById(R.id.fab);
        fab2 = view.findViewById(R.id.fab2);
        progressBar = view.findViewById(R.id.progressBar3);
        completeLvl = view.findViewById(R.id.progressText);
        adView = view.findViewById(R.id.adView);

        setUserData();
        setAds();
        setRewardedVideo();
        onButtonClick();


        return view;
    }


    private void showProgress(final boolean show) {
        if (show) {
            Loading loading = Loading.newInstance();
            if (getActivity().getSupportFragmentManager().findFragmentByTag("Loading") == null)
                getActivity().getSupportFragmentManager().beginTransaction().add(loading, "Loading").commit();
        } else {
            Loading loading = (Loading) getActivity().getSupportFragmentManager().findFragmentByTag("Loading");
            if (loading != null && loading.isVisible()) loading.dismiss();
        }

    }

    private void startQuestion() {
        questionData = new QuestionData();
        getQuestion = new GetQuestion(getString(R.string.site_address), userData, questionData, getActivity().getSupportFragmentManager(), getActivity().getBaseContext());
        getQuestion.execute((Void) null);
    }

    private void onButtonClick() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQuestion();
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                final Handler handler2 = new Handler();
                handler2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAd.loadAd(getResources().getString(R.string.ads_reward_main_id), new AdRequest.Builder().build());
                    }
                }, 300);
                System.out.println("test");
            }
        });
    }


    private void setAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }


    public void setUserData() {
        Glide.with(this).load(userData.getUserAvatar()).into(imageView);

        textUserLvl.setText(userData.getRankName());
        textUserNextLvl.setText(userData.getRankNext());
        textUserPoints.setText(userData.getUserPoints());
        textUserName.setText(userData.getUsername());
        textUserNextPoints.setText(userData.getUserPointsNext());

        Integer userPointsInt;
        Integer userPointsNextInt;
        Integer userPointsCurrentRankInt;

        try {
            userPointsInt = Integer.parseInt(userData.getUserPoints());
            userPointsNextInt = Integer.parseInt(userData.getUserPointsNext());
            userPointsCurrentRankInt = Integer.parseInt(userData.getPointsCurrentRank());
        } catch (NumberFormatException e) {
            userPointsInt = 1;
            userPointsNextInt = 1;
            userPointsCurrentRankInt = 0;
        }

        if (userPointsNextInt == 0) userPointsNextInt = 1;


        Integer userRating = (((userPointsInt - userPointsCurrentRankInt) * 100) / (userPointsNextInt - userPointsCurrentRankInt));

        if (userRating >= 100) userRating = 100;
        if (userRating <= 0) userRating = 0;


        progressBar.setProgress(userRating);
        progressBar.setScaleY(9f);


        String progress = userRating.toString() + "%";
        completeLvl.setText(progress);


    }


    @Override
    public void onRewardedVideoAdLoaded() {
        REWARDED = false;
        mAd.show();
    }

    @Override
    public void onRewardedVideoAdOpened() {
        showProgress(false);
    }

    @Override
    public void onRewardedVideoStarted() {
        showProgress(false);
    }

    @Override
    public void onRewardedVideoAdClosed() {
        if (REWARDED) {
            mPasswordTask = new RenewUserAnswer(getString(R.string.site_address), userData.getCookie(), userData.getUserId(), getActivity().getSupportFragmentManager());
            mPasswordTask.execute();
        } else {
            showProgress(false);
        }
    }

    @Override
    public void onRewarded(RewardItem rewardItem) {
        REWARDED = true;
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
        showProgress(false);
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {
        showProgress(false);
        Toast.makeText(getActivity(), "Błąd połaczenia lub wykorzystałeś dzienny limit", Toast.LENGTH_SHORT).show();
    }
}
