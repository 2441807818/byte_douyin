package com.byteteam.douyin.logic.repository;

import com.byteteam.douyin.MyApplication;
import com.byteteam.douyin.logic.dataSource.ClientTokenDataSource;
import com.byteteam.douyin.logic.database.dao.ClientTokenDao;
import com.byteteam.douyin.logic.database.model.ClientToken;
import com.byteteam.douyin.logic.factory.NetWorkFactory;
import com.byteteam.douyin.logic.network.response.ResponseTransformer;
import com.byteteam.douyin.logic.network.service.TokenService;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @introduction： ClientToken仓库类
 * @author： 林锦焜
 * @time： 2022/8/8 20:03
 */
public class ClientTokenRepository implements ClientTokenDataSource {

    ClientTokenDao clientTokenDao;

    public ClientTokenRepository(ClientTokenDao clientTokenDao) {
        this.clientTokenDao = clientTokenDao;
    }

    /**
     * 获取ClientToken
     * @return Maybe<ClientToken>
     */
    @Override
    public Maybe<ClientToken> getClientToken() {
        return clientTokenDao.getClientToken(System.currentTimeMillis())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                // 当数据库无数据时会发送这条默认数据
                .defaultIfEmpty(new ClientToken())
                .flatMap(clientToken -> {
                    // 说明本地数据库无数据
                    if (clientToken.getAccessToken() == null) {
                        // 联网获取数据
                        return requestClientToken().singleElement();
                    }
                    // 说明是数据库中的数据
                    return Maybe.just(clientToken);
                });
    }

    // 保存一条ClientToken
    @Override
    public Completable insert(ClientToken clientToken) {
        return clientTokenDao.insert(clientToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // 联网申请ClientToken
    public Observable<ClientToken> requestClientToken() {
        Retrofit retrofit = NetWorkFactory.provideRetrofit();
        TokenService tokenService = retrofit.create(TokenService.class);
        return tokenService.getClientToken(MyApplication.clientkey,MyApplication.clientSecret,"client_credential")
                .compose(ResponseTransformer.obtain())
                .map(data -> {
                    // 保存AccessToken
                    ClientToken clientToken = new ClientToken();
                    clientToken.setAccessToken(data.getAccessToken());
                    clientToken.setExpiresIn(System.currentTimeMillis() + data.getExpiresIn() * 1000);
                    // 添加到数据库
                    insert(clientToken).subscribe();
                    return clientToken;
                });
    }
}
