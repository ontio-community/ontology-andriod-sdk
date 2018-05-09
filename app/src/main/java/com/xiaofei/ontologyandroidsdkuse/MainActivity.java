package com.xiaofei.ontologyandroidsdkuse;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.github.ontio.OntSdk;
import com.github.ontio.network.exception.ConnectorException;
import com.github.ontio.sdk.wallet.Identity;

import org.spongycastle.jcajce.provider.symmetric.Threefish;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.i("aaa", "onCreate: ");


//        OkHttpClient client = new OkHttpClient();
//        Request request = new Request.Builder()
//                .url("http://www.baidu.com")
//                .build();
//
//
//        Response response = null;
//        try {
//            response = client.newCall(request).execute();
//            String string = response.body().string();
//            Log.i("", "run: "+string);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        FutureTask<Integer> futureBlockHeight = new FutureTask<>(new Callable<Integer>() {
////            @Override
////            public Integer call() throws Exception {
////                OntSdk ontSdk = OntSdk.getInstance();
////                ontSdk.setRestful("http://13.78.112.191:20334");
////
////                int blockHeight = ontSdk.getConnectMgr().getBlockHeight();
////                return blockHeight;
////            }
////        });
////
////        new Thread(futureBlockHeight).start();
////        Flowable.just(futureBlockHeight).subscribe(new Consumer<FutureTask<Integer>>() {
////            @Override
////            public void accept(FutureTask<Integer> integerFutureTask) throws Exception {
////                myBlockHeight = integerFutureTask.get();
////                Log.i("", "alalheight: ");
////            }
////        });
////
////        Log.i("mid", "mid: ");



        FutureTask<Identity> identityFutureTask = new FutureTask<>(new Callable<Identity>() {
            @Override
            public Identity call() throws Exception {
                OntSdk ontSdk = OntSdk.getInstance();
                ontSdk.setRestful("http://polaris1.ont.io:20334");
                String filepath = Environment.getExternalStoragePublicDirectory("") + "/wallet.json";
                //ontSdk.openWalletFile(filepath);
                ontSdk.setCodeAddress("80b0cc71bda8653599c5666cae084bff587e2de1");

                //Identity identity = ontSdk.getWalletMgr().createIdentity("123456");

                Identity identity1 = ontSdk.getOntIdTx().sendRegisterPreExec("123456");
                return identity1;
            }
        });

        new Thread(identityFutureTask).start();
        Flowable.just(identityFutureTask).subscribe(new Consumer<FutureTask<Identity>>() {
            @Override
            public void accept(FutureTask<Identity> identityFutureTask) throws Exception {
                myIdentity = identityFutureTask.get();
                Log.i("haha", "myIdentity: "+myIdentity.toString());
            }
        });

    }

    int myBlockHeight;
    Identity myIdentity;
}

