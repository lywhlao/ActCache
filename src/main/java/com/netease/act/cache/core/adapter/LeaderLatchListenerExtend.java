package com.netease.act.cache.core.adapter;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.leader.CancelLeadershipException;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;

public abstract class LeaderLatchListenerExtend implements LeaderLatchListener,ConnectionStateListener {

    @Override
    public abstract void isLeader();

    @Override
    public abstract void notLeader();

    @Override
    public void stateChanged(CuratorFramework client, ConnectionState newState) {
        if ( client.getConnectionStateErrorPolicy().isErrorState(newState) )
        {
            throw new CancelLeadershipException();
        }
    }
}