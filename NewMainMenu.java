package android.qrz.activity.main_menu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.qrz.R;
import android.qrz.activity.AboutAppActivity;
//import android.qrz.activity.Callbook;
import android.qrz.activity.CompetitionsActivity;
import android.qrz.activity.ForumActivity;
import android.qrz.activity.FrequencyActivity;
import android.qrz.activity.LoginActivity;
import android.qrz.activity.NewCallbook;
import android.qrz.activity.News;
import android.qrz.activity.declarations.BillboardActivity;
import android.qrz.activity.main_menu.data_source.DataSourceNews;
import android.qrz.activity.new_diploms.ui.MainDiplomActivity;

import android.qrz.activity.news.FragmentNewsListPart2;
import android.qrz.activity.personal_activity.PersonalActivity;
//import android.qrz.activity.personal_actvity.personal_controller.DeclarationPageDataSource;
import android.qrz.activity.qsl.QslActivity;
import android.qrz.fragment.ProgressFragment;
import android.qrz.model.CheckToken;
import android.qrz.model.Token;
import android.qrz.rest_service.api.RetrofitV2Config;

import android.qrz.utils.AllertUtils;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NewMainMenu extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private static final String IS_PROGRESS_SHOWING = "IS_PROGRESS_SHOWING";
    private static final String TOKEN_FOR_CHECK = "TOKEN_FOR_CHECK";
    public static final String FAKE_TOKEN = "000000000";

    private FragmentNewsListPart2 fregmentNewsList;

    private ProgressFragment progress;
    private DrawerLayout drawer;

    private RadioButton btnNews;
    private RadioButton btnCalendar;
    private ImageView login;
    private ImageView logout;
    private ImageView personalOffice;
    private LinearLayout loginLayout;
    private LinearLayout logoutLayout;
    private LinearLayout personalOfficeLayout;

    public static boolean isProgressShowing;
    private String tokenForCheck;
    private String tokenfromLogin;

    //region Life Cycle Activity
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_menu_app_bar_toolbar);
        setSupportActionBar(toolbar);


        btnNews = (RadioButton) findViewById(R.id.activity_main_menu_btnNews);
        btnCalendar = (RadioButton) findViewById(R.id.activity_main_menu_btnCalendar);
        drawer = (DrawerLayout) findViewById(R.id.activity_main_menu_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.activity_main_menu_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.action_main_menu_news).setVisible(false);


        loginLayout = (LinearLayout)findViewById(R.id.navigation_menu_layout_Login);
        logoutLayout = (LinearLayout)findViewById(R.id.navigation_menu_layout_Logout);
        personalOfficeLayout = (LinearLayout)findViewById(R.id.navigation_menu_layout_PersonalOffice);

        login = (ImageView) findViewById(R.id.navigation_drawer_ivLogin);
        login.setOnClickListener(this);
        logout = (ImageView) findViewById(R.id.navigation_drawer_ivLogout);
        logout.setOnClickListener(this);
        personalOffice = (ImageView) findViewById(R.id.navigation_drawer_ivPersonalOffice);
        personalOffice.setOnClickListener(this);


        btnNews.setOnClickListener(this);
        btnCalendar.setOnClickListener(this);

        if (savedInstanceState == null) {
            fregmentNewsList = FragmentNewsListPart2.newInstance();
            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_menu_container, fregmentNewsList, FragmentNewsListPart2.TAG).commit();
            Log.d("SAVEDINSTANCE","1");
//            fragmentMainMenu = FragmentMainMenu.newInstance();
//            getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_menu_container, fragmentMainMenu, FragmentMainMenu.TAG).commit();
        } else {
            Log.d("SAVEDINSTANCE","2");
            tokenForCheck = savedInstanceState.getString(TOKEN_FOR_CHECK);
            isProgressShowing = savedInstanceState.getBoolean(IS_PROGRESS_SHOWING);
            if (isProgressShowing == true) requestCheckToken(tokenForCheck);
            else return;
        }
    }


        @Override
    public void onBackPressed() {
        Log.d("QRZ", "onBackPressed Called");
        AllertUtils.exitAllert(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(LoginActivity.getIsLogin()){
            Log.i("TOKEN FROM LOGIN","" + tokenfromLogin);
            Log.i("TOKEN ",Token.getSavedToken());
            logoutLayout.setVisibility(View.VISIBLE);
            personalOfficeLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }else {
            logoutLayout.setVisibility(View.GONE);
            personalOfficeLayout.setVisibility(View.GONE);
            loginLayout.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_PROGRESS_SHOWING, isProgressShowing);
        outState.putString(TOKEN_FOR_CHECK, tokenForCheck);

    }


    //endregion

    //region Listener Navigation Menu
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_main_menu_callbook:
                startActivity(new Intent(NewMainMenu.this, NewCallbook.class));
                return true;
            case R.id.action_main_menu_diploms:
                startActivity(new Intent(NewMainMenu.this, MainDiplomActivity.class));
                return true;
            case R.id.action_main_menu_competitions:
                startActivity(new Intent(NewMainMenu.this, CompetitionsActivity.class));
                return true;
            case R.id.action_main_menu_ad:
                startActivity(new Intent(NewMainMenu.this, BillboardActivity.class));
                return true;
            case R.id.action_main_menu_freq:
                startActivity(new Intent(NewMainMenu.this, FrequencyActivity.class));
                return true;
//            case R.id.action_main_menu_news:
//                startActivity(new Intent(NewMainMenu.this, News.class));
//                return true;
            case R.id.action_main_menu_qsl:
                startActivity(new Intent(NewMainMenu.this, QslActivity.class));
                return true;
            case R.id.action_main_menu_forum:
                startActivity(new Intent(NewMainMenu.this, ForumActivity.class));
                return true;
            case R.id.action_main_menu_aboutApp:
                startActivity(new Intent(NewMainMenu.this, AboutAppActivity.class));
                return true;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion

    //region Utils
    private void requestCheckToken(String token) {
        showProgress();
        checkTokenRequest(token);

    }

    public void showProgress() {
        isProgressShowing = true;
        getSupportFragmentManager().beginTransaction().replace(R.id.progressContainer,
                ProgressFragment.newInstance(), ProgressFragment.TAG).commit();
    }


    public void dismiss() {
        progress = (ProgressFragment) getSupportFragmentManager().findFragmentByTag(ProgressFragment.TAG);
        if (progress != null) {
            isProgressShowing = false;
            getSupportFragmentManager().beginTransaction().remove(progress).commit();
        }
    }

    private void createDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_personal_message));
        builder.setCancelable(true);
        builder.setNegativeButton(getResources().getString(R.string.button_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.button_login), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(NewMainMenu.this, LoginActivity.class);
                startActivity(intent);
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dismiss();
        dialog.show();
    }
    //endregion

    //region Callback From Server
    private void checkTokenRequest(String token) {
        Call<CheckToken> tokenCall = RetrofitV2Config.getService().checkToken(token);
        tokenCall.enqueue(new Callback<CheckToken>() {
            @Override
            public void onResponse(Call<CheckToken> call, Response<CheckToken> response) {
                if (LoginActivity.getIsLogin() && Token.getSavedToken() != null && response.body().isValidToken()) {
                    dismiss();
                    Intent intent = new Intent(NewMainMenu.this, PersonalActivity.class);
                    startActivity(intent);
                } else {
                    createDialog();
                }
            }

            @Override
            public void onFailure(Call<CheckToken> call, Throwable t) {
                createDialog();
            }
        });
    }

    //endregion

    //region Listeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_main_menu_btnNews:
                FragmentNewsListPart2 newsListPart2 = (FragmentNewsListPart2) getSupportFragmentManager().findFragmentByTag(FragmentNewsListPart2.TAG);
                if (newsListPart2 == null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_menu_container, FragmentNewsListPart2.getInstance(), FragmentNewsListPart2.TAG).commit();
                    DataSourceNews.getInstance().getAdapter().notifyDataSetChanged();
                }else {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_menu_container, newsListPart2, FragmentNewsListPart2.TAG).commit();
                    DataSourceNews.getInstance().getAdapter().notifyDataSetChanged();
                }break;
            case R.id.activity_main_menu_btnCalendar:
                FragmentCalendarMain fragmentCalendarMain = (FragmentCalendarMain) getSupportFragmentManager().findFragmentByTag(FragmentCalendarMain.TAG);
                if (fragmentCalendarMain == null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_menu_container, FragmentCalendarMain.newInstance(), FragmentCalendarMain.TAG).commit();
                } else
                    getSupportFragmentManager().beginTransaction().replace(R.id.activity_main_menu_container, fragmentCalendarMain, FragmentCalendarMain.TAG).commit();
                break;
            case R.id.navigation_drawer_ivLogin:
                Intent intent = new Intent(NewMainMenu.this, LoginActivity.class);
                startActivityForResult(intent, 1);
                break;
            case R.id.navigation_drawer_ivPersonalOffice:
                if (Token.getSavedToken() != null) {
                    Log.i("TOKEN IN MAIN MENU",Token.getSavedToken());
                    tokenForCheck = Token.getSavedToken();}
                else tokenForCheck = FAKE_TOKEN;
                requestCheckToken(tokenForCheck);
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.navigation_drawer_ivLogout:
                Log.i("LOGOUT","CLICKED");
                logoutLayout.setVisibility(View.GONE);
                personalOfficeLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                Token.initToken(NewMainMenu.this.getApplicationContext()).setToken(FAKE_TOKEN);
                LoginActivity.setIsLogin(false);
                break;
        }
    }
    //endregion

    //region Login CallbacK
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) return;
        else {
            tokenfromLogin = data.getStringExtra(LoginActivity.TOKEN);
            Log.i("INTENT",tokenfromLogin);
            logoutLayout.setVisibility(View.VISIBLE);
            personalOfficeLayout.setVisibility(View.VISIBLE);
            loginLayout.setVisibility(View.GONE);
        }
    }
    //endregion
}
