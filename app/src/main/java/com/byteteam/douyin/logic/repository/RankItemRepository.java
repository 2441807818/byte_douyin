package com.byteteam.douyin.logic.repository;


import com.byteteam.douyin.logic.dataSource.ClientTokenDataSource;
import com.byteteam.douyin.logic.dataSource.RankItemDataSource;
import com.byteteam.douyin.logic.database.dao.RankItemDao;
import com.byteteam.douyin.logic.database.model.ClientToken;
import com.byteteam.douyin.logic.database.model.RankItem;
import com.byteteam.douyin.logic.factory.NetWorkFactory;
import com.byteteam.douyin.logic.network.model.RankData;
import com.byteteam.douyin.logic.network.response.DouYinResponse;
import com.byteteam.douyin.logic.network.response.ResponseTransformer;
import com.byteteam.douyin.logic.network.service.RankService;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * @introduction： RankItem仓库类
 * @author： 林锦焜
 * @time： 2022/8/10 20:43
 */
public class RankItemRepository implements RankItemDataSource {

    ClientTokenDataSource clientTokenDataSource;

    RankItemDao rankItemDao;

    public RankItemRepository(ClientTokenDataSource clientTokenDataSource, RankItemDao rankItemDao) {
        this.clientTokenDataSource = clientTokenDataSource;
        this.rankItemDao = rankItemDao;
    }

    @Override
    public Maybe<List<RankItem>> queryMovie(int type, int version) {
        // 首先请求获取clientToken，如果数据库的未过期会从数据库中拿，数据库过期会联网申请
        return clientTokenDataSource.getClientToken()
                .flatMap((Function<ClientToken, MaybeSource<List<RankItem>>>) clientToken -> {
                    // 成功获取ClientToken
                    Retrofit retrofit = NetWorkFactory.provideRetrofit();
                    RankService rankService = retrofit.create(RankService.class);
                    // 根据是否需要版本号选择请求方式
                    Observable<DouYinResponse<RankData<RankItem>>> observable
                            = version == 0 ? rankService.getRank(clientToken.getAccessToken(), type) // 获取最新
                            : rankService.getRank(clientToken.getAccessToken(), type, version); // 根据版本号获取
                    return observable
                            .compose(ResponseTransformer.obtain())
                            .map(rankItemRankData -> {
                                // 如果map被调用，则说明联网请求成功，将结果异步缓存到数据库
                                // 保存数据到数据库，并清空之前的缓存
                                Disposable disposable = rankItemDao.delete(type, version)
                                        .subscribeOn(Schedulers.io())
                                        .subscribe(new Action() {
                                            @Override
                                            public void run() {
                                                // 给榜单设置版本号
                                                for (RankItem rankItem : rankItemRankData.getList()) {
                                                    rankItem.setVersion(version);
                                                }
                                                rankItemDao.insert(rankItemRankData.getList())
                                                        .subscribeOn(Schedulers.io())
                                                        .subscribe();
                                            }
                                        });
                                return rankItemRankData.getList();
                            })
                            .singleElement();
                })
                .onErrorResumeNext((Function<Throwable, MaybeSource<List<RankItem>>>) throwable -> {
                    // 如果请求出现异常(联网失败等情况)
                    return rankItemDao.queryRank(type, version)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .defaultIfEmpty(new ArrayList<>())
                            // 当数据库无数据时会发送这条默认数据
                            .flatMap((Function<List<RankItem>, MaybeSource<List<RankItem>>>) rankItems -> {
                                // 说明本地数据库无数据
                                if (rankItems.size() == 0) {
                                    // 抛出上面的错误
                                    return Maybe.error(throwable);
                                }
                                // 说明是数据库中的数据
                                return Maybe.just(rankItems);
                            });
                });
    }

}
