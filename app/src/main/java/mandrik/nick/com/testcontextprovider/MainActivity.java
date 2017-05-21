package mandrik.nick.com.testcontextprovider;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    final static int OPERATION_SHOW_LAST = 1;
    final static int OPERATION_SHOW_ALL = 2;
    final static int OPERATION_SHOW_LAST_DAY = 3;
    final static int OPERATION_SHOW_INSERT = 4;

    final static String PROVIDER_URI_STRING ="content://com.yandex.mandrik.launcher";

    private Toast toast;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    private boolean isExistProviderPermissions(int operation) {
        String[] permissions = getPermissionsByOperation(operation);

        ArrayList<String> requestedPermissions = new ArrayList();
        for(int i = 0; i < permissions.length; i++) {
            if (checkCallingOrSelfPermission(permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                requestedPermissions.add(permissions[i]);
            }
        }
        if (requestedPermissions.size() != 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                String[] req = new String[requestedPermissions.size()];
                req = requestedPermissions.toArray(req);
                ActivityCompat.requestPermissions(MainActivity.this, req, operation);
            } else {
                showErrorPermissionMessage();
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        boolean isGranted = true;

        if (grantResults.length > 0) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    isGranted = false;
                }
            }
        } else {
            isGranted = false;
        }

        if (isGranted) {
            switch (requestCode) {
                case OPERATION_SHOW_ALL:
                    showAll();
                    break;
                case OPERATION_SHOW_LAST:
                    showLastRecord();
                    break;
                case OPERATION_SHOW_LAST_DAY:
                    showLastDayRecords();
                    break;
                case OPERATION_SHOW_INSERT:
                    insert();
                    break;
            }
        } else {
            showErrorPermissionMessage();
        }
    }

    public void onClickInsert(View v) {
        if(isExistProviderPermissions(OPERATION_SHOW_INSERT)) {
            insert();
        }
    }

    public void onClickShowAll(View v) {
        if(isExistProviderPermissions(OPERATION_SHOW_ALL)) {
            showAll();
        }

    }

    public void onClickShowLastRecord(View v) {
        if(isExistProviderPermissions(OPERATION_SHOW_LAST)) {
            showLastRecord();
        }
    }

    public void onClickShowRecordsInDay(View v) {
        if(isExistProviderPermissions(OPERATION_SHOW_LAST_DAY)) {
            showLastDayRecords();
        }
    }

    private void showLastRecord() {
        Uri uri = Uri.withAppendedPath(Uri.parse(PROVIDER_URI_STRING), "uri/last");
        Cursor cursor = getContentResolver().query(uri,
                null, null, null, null);
        TextView textView = (TextView) findViewById(R.id.lvUri);
        textView.setText("");
        if (cursor.moveToFirst()) {
            do {
                String cursorUri = cursor.getString(0);
                if(cursorUri != null) {
                    Log.d("ReadLastRecord", cursorUri);
                    textView.setText(textView.getText() + "\n" + cursorUri);
                }
            } while (cursor.moveToNext());
        }
    }

    private void showLastDayRecords() {
        Uri uri = Uri.withAppendedPath(Uri.parse(PROVIDER_URI_STRING), "uri/last_day");
        Cursor cursor = getContentResolver().query(uri,
                null, null, null, null);
        TextView textView = (TextView) findViewById(R.id.lvUri);
        textView.setText("");
        if (cursor.moveToFirst()) {
            do {
                String cursorUri = cursor.getString(0);
                if(cursorUri != null) {
                    Log.d("ReadLastDayRecords", cursorUri);
                    textView.setText(textView.getText() + "\n" + cursorUri);
                }
            } while (cursor.moveToNext());
        }
    }

    private void showAll() {
        Uri uri = Uri.withAppendedPath(Uri.parse(PROVIDER_URI_STRING), "uri/all");
        Cursor cursor = getContentResolver().query(uri,
                null, null, null, null);
        TextView textView = (TextView) findViewById(R.id.lvUri);
        textView.setText("");
        if (cursor.moveToFirst()) {
            do {
                String cursorUri = cursor.getString(0);
                if(cursorUri != null) {
                    Log.d("ReadAllRecords", cursorUri);
                    textView.setText(textView.getText() + "\n" + cursorUri);
                }
            } while (cursor.moveToNext());
        }
    }

    private void insert() {
        RelativeLayout linearLayout = new RelativeLayout(MainActivity.this);
        final EditText editText = new EditText(MainActivity.this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(editText,numPicerParams);

        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle(R.string.enter_uri);
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.add,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                ContentValues values = new ContentValues();
                                values.put("value", String.valueOf(editText.getText()));
                                Uri uri = Uri.withAppendedPath(Uri.parse(PROVIDER_URI_STRING), "uri");
                                getContentResolver().insert(uri, values);
                                Log.d("InsertRecord", String.valueOf(editText.getText()));
                                if (toast != null) {
                                    toast.cancel();
                                }
                                toast = Toast.makeText(MainActivity.this,
                                        getResources().getString(R.string.uri_should_be_added) +
                                                "\n" + getResources().getString(R.string.update_list_of_uris),
                                                Toast.LENGTH_LONG);
                                toast.show();
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String[] getPermissionsByOperation(int operation) {
        switch (operation) {
            case OPERATION_SHOW_LAST:
                return new String[] {
                        getResources().getString(R.string.provider_permission)
                };
            case OPERATION_SHOW_ALL:
                return new String[] {
                        getResources().getString(R.string.provider_permission),
                        getResources().getString(R.string.provider_permission_read_all)
                };
            case OPERATION_SHOW_LAST_DAY:
                return new String[] {
                        getResources().getString(R.string.provider_permission)
                };
            case OPERATION_SHOW_INSERT:
                return new String[] {
                        getResources().getString(R.string.provider_permission),
                        getResources().getString(R.string.provider_permission_write)
                };
            default:
                return null;
        }
    }

    private void showErrorPermissionMessage() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, getResources().getString(R.string.permissions_are_not_granted),
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
