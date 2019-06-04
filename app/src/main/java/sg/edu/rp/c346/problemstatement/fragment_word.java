package sg.edu.rp.c346.problemstatement;


import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class fragment_word extends Fragment {
    Button btnsendsms,btnsendemail;
    TextView tvshowsms;
    EditText etword;


    public fragment_word() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_word, container, false);
        btnsendsms = view.findViewById(R.id.btnsendsms);
        btnsendemail = view.findViewById(R.id.buttonsendemail);
        tvshowsms = view.findViewById(R.id.tvshowsms);
        etword = view.findViewById(R.id.etword);
        int permissionCheck = PermissionChecker.checkSelfPermission
                (getContext(), Manifest.permission.READ_SMS);

        if (permissionCheck != PermissionChecker.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_SMS}, 0);
            // stops the action from proceeding further as permission not
            //  granted yet
            return view;
        }
        btnsendemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "dkguys520@gmail.com" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "SMS content");
                String text1 = tvshowsms.getText().toString();
                intent.putExtra(Intent.EXTRA_TEXT, text1 );

                startActivity(Intent.createChooser(intent, "Email via..."));

            }
        });
        btnsendsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create all messages URI
                Uri uri = Uri.parse("content://sms");
                // The columns we want
                //  date is when the message took place
                //  address is the number of the other party
                //  body is the message content
                //  type 1 is received, type 2 sent
                String[] reqCols = new String[]{"date", "address", "body", "type"};

                // Get Content Resolver object from which to
                //  query the content provider
                ContentResolver cr = getActivity().getContentResolver();
                // Fetch SMS Message from Built-in Content Provider
                // The filter String
                String filter = "body LIKE ?";
                // The matches for the ?
                String word = etword.getText().toString();
                String[] arr = word.split(" ");
                String[] filterargs = new String[arr.length];

                for (int i = 0; i < arr.length ;i++ ){
                     filterargs[i] = "%" + arr[i] + "%";

                     filter+= "OR body LIKE ?";
                }


                Cursor cursor = cr.query(uri, reqCols, filter, filterargs, null);
                String smsBody = "";
                if (cursor.moveToFirst()) {
                    do {
                        long dateInMillis = cursor.getLong(0);
                        String date = (String) DateFormat
                                .format("dd MMM yyyy h:mm:ss aa", dateInMillis);
                        String address = cursor.getString(1);
                        String body = cursor.getString(2);
                        String type = cursor.getString(3);
                        if (type.equalsIgnoreCase("1")) {
                            type = "Inbox:";
                        } else {
                            type = "Sent:";
                        }
                        smsBody += type + " " + address + "\n at " + date
                                + "\n\"" + body + "\"\n\n";
                    } while (cursor.moveToNext());
                }
                tvshowsms.setText(smsBody);
            }
        });

        // Inflate the layout for this fragment
        return view;

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 0: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the read SMS
                    //  as if the btnRetrieve is clicked
                    btnsendsms.performClick();

                } else {
                    // permission denied... notify user
                    Toast.makeText(getContext(), "Permission not granted",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}