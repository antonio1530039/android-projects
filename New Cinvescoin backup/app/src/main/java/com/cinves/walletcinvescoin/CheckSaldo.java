package com.cinves.walletcinvescoin;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CheckSaldo extends AsyncTask<Cartera, Void, String> {

    private ProgressDialog dialog;
    View txt;

    public CheckSaldo(Context c, View txt){
        dialog = new ProgressDialog(c);
        this.txt = txt;
    }



    @Override
    protected String doInBackground(Cartera... carteras) {
        BigDecimal bd = new BigDecimal(carteras[0].miSaldo());
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return ""+bd;

    }

    /** application context. */
    @Override
    protected void onPreExecute() {
        this.dialog.setMessage("Calculando balance...");
        this.dialog.show();
    }


    @Override
    protected void onPostExecute(final String x) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        ((TextView) this.txt).setText(x + " CC");
        // Setting data to list adapter
    }
}
