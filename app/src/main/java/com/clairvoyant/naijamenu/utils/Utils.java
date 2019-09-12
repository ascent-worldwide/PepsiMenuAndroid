package com.clairvoyant.naijamenu.utils;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.clairvoyant.naijamenu.LoginActivity;
import com.clairvoyant.naijamenu.R;
import com.clairvoyant.naijamenu.fonts.RobotoRegularTextView;
import com.clairvoyant.naijamenu.utils.BasicImageDownloader.OnImageReadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final String TAG = "PEPSI_MENU";
    static String regEx_email = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@" + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\." + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?" + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

    public static boolean isOnline(Context mContext) {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public static void longInfo(String str) {

        if (str.length() > 8000) {
            Log.i(TAG, str.substring(0, 8000));
            longInfo(str.substring(8000));
        } else
            Log.i(TAG, str);
    }

    public static String getAppVersion(Context mContext) {
        String appVersion = null;
        try {
            PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            appVersion = String.valueOf(packageInfo.versionCode);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appVersion;
    }

//    public static String getIMEI(Context context) {
//        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        return telephonyManager.getDeviceId();
//    }

    public static String getMake() {
        return android.os.Build.MANUFACTURER;
    }

    public static String getModel() {
        return android.os.Build.MODEL;
    }

    public static Dialog showProgressDialog(Context mContext) {
        Dialog progressDialog = new Dialog(mContext);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_view);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public static void cancelProgressDialog(Dialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public static void setGCMId(Context context, String gcmId) {
        PreferencesUtils.putString(context, Constants.GCM_ID, gcmId);
    }

    public static String getGCMId(Context context) {
        return PreferencesUtils.getString(context, Constants.GCM_ID, "");
    }

	/*public static boolean isValidEmail(String email)
	{
		if (TextUtils.isEmpty(email))
		{
			return false;
		}
		else
		{
			return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
		}
	}*/

    /**
     * convert date format
     *
     * @param date_value
     * @param newformat
     * @param oldformat
     * @return
     */
    public static String ConvertDateFormat(String date_value, String newformat, String oldformat) {
        String final_format = null;

        SimpleDateFormat format_old = new SimpleDateFormat(oldformat);
        SimpleDateFormat format_new = new SimpleDateFormat(newformat);
        Date date = null;

        try {
            date = format_old.parse(date_value);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final_format = format_new.format(date);

        return final_format;
    }

    public static Dialog showNoConnectionDialog(Context mContext, int resource) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.no_internet_dialog);
        RobotoRegularTextView messageView = (RobotoRegularTextView) dialog.findViewById(R.id.message);
        messageView.setText(resource);
		/*RobotoRegularTextView tvTryAgain = (RobotoRegularTextView) dialog.findViewById(R.id.tvTryAgain);
		RobotoRegularTextView ok = (RobotoRegularTextView) dialog.findViewById(R.id.ok);
		tvTryAgain.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
				validateFields();
			}
		});

		ok.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				dialog.dismiss();
			}
		});
		dialog.show();*/
        return dialog;

    }

    public static boolean Validate_EMail(Context context, String email, String message) {
        boolean status = false;
        if (email.length() != 0) {

            Matcher matcherObj_email = Pattern.compile(regEx_email).matcher(email);

            if (matcherObj_email.matches()) {
                status = true;

            } else {
                status = false;

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }

        } else {
            status = false;
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
        return status;
    }

    public static void setOrientation(Context mContext) {
        int orientation = PreferencesUtils.getInt(mContext, Constants.ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        if (orientation == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE)
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        else
            ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    public static String getYouTubeId(String youTubeUrl) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(youTubeUrl);
        if (matcher.find()) {
            return matcher.group();
        } else {
            return "error";
        }
    }

    public static void logout(Context mContext) {
        PreferencesUtils.removeValue(mContext, Constants.LOGGED);
        PreferencesUtils.removeValue(mContext, Constants.RESTAURANT_ID);
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Activity) mContext).startActivity(intent);
        ((Activity) mContext).finish();
    }

    public static void downloadVideo(Context context, String videoUrl, String fileName) {
        try {
            String RootDir = getVideoDirectoryPath(context);
            File RootFile = new File(RootDir);
            RootFile.mkdir();
            // File root = Environment.getExternalStorageDirectory();
            URL u = new URL(videoUrl);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(new File(RootFile, fileName));
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;

            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();


        } catch (Exception e) {
            Log.d("Error....", e.toString());
        }
    }

    public static void downloadFile(Context context, String videoUrl, String fileName) {
        String path = getVideoDirectoryPath(context);
        File direct = new File(path);

//	    File direct = new File(Environment.getExternalStorageDirectory() + "/AnhsirkDasarp");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(videoUrl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Demo")
                .setDescription("Something useful. No, really.")
                .setDestinationInExternalPublicDir(path, fileName);

        mgr.enqueue(request);
    }

    public static String getVideoDirectoryPath(Context context) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + ".files";
        return path;
    }

    /**
     * Method is used to render the image from local storage or picasso
     *
     * @param pContext
     * @param pImgUrl
     * @param pImageView
     */
    public static void renderImage(final Context pContext, final String pImgUrl, final ImageView pImageView) {
        if (pImgUrl != null && !pImgUrl.isEmpty()) {
            try {
                final String imgUrl = URLDecoder.decode(pImgUrl, "UTF-8");

                final String fileName = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
                final Bitmap.CompressFormat mFormat = Bitmap.CompressFormat.JPEG;
                /* don't forget to include the extension into the file name */
                final File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator + "Pepsi_Menu" + File.separator + fileName + "." + mFormat.name().toLowerCase());

                if (imgFile.exists()) {
                    BasicImageDownloader.readFromDiskAsync(imgFile, new OnImageReadListener() {
                        @Override
                        public void onReadFailed() {
                            Log.e("error", "BasicImageDownloader error in image reading..");
                            renderImageUsingGlide(pContext, imgUrl, pImageView);
                        }

                        @Override
                        public void onImageRead(Bitmap bitmap) {
                            if (bitmap != null) {
                                int ivWidth = pImageView.getWidth();
                                int ivHeight = pImageView.getHeight();
                                Bitmap newbitMap = Bitmap.createScaledBitmap(bitmap, ivWidth, ivHeight, true);
                                pImageView.setImageBitmap(newbitMap);
                                Log.i("success", "BasicImageDownloader image read successfully");
                            }
                        }
                    });
                } else {
                    Log.e("error", "BasicImageDownloader image not exist");
                    renderImageUsingGlide(pContext, imgUrl, pImageView);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method is used to render image using picasso
     *
     * @param pContext
     * @param pImgUrl
     * @param pImageView
     */
    private static void renderImageUsingGlide(Context pContext, String pImgUrl, final ImageView pImageView) {
       /* Picasso.with(pContext).load(pImgUrl).error(R.drawable.product_placeholder).resize(200, 200).placeholder(R.drawable.product_placeholder).into(pImageView, new Callback() {
            @Override
            public void onSuccess() {
                Log.d("image load", "success");
            }

            @Override
            public void onError() {
                Log.d("image load", "fail");
            }
        });*/
        Glide.with(pContext).load(pImgUrl).fitCenter().placeholder(R.drawable.product_placeholder).fitCenter().into(pImageView);
    }
}