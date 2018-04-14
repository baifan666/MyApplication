package com.example.baifan.myapplication.utils;

import android.content.Context;
import android.view.View;

import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;

/**
 * Created by baifan on 2018/4/14.
 */

public class MyConversationListBehaviorListener implements RongIM.ConversationListBehaviorListener{
    /**
     * 当点击会话头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param s - 被点击的用户id。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    /**
     * 当长按会话头像后执行。
     *
     * @param context          上下文。
     * @param conversationType 会话类型。
     * @param s - 被点击的用户id。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    /**
     * 长按会话列表中的 item 时执行。
     *
     * @param context          上下文。
     * @param uiConversation - 长按时的会话条目。
     * @param view - 触发点击的 View。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        uiConversation.getConversationSenderId();
        return false;
    }

    /**
     * 点击会话列表中的 item 时执行。
     *
     * @param context          上下文。
     * @param uiConversation - 长按时的会话条目。
     * @param view - 触发点击的 View。
     * @return 如果用户自己处理了点击后的逻辑，则返回 true，否则返回 false，false 走融云默认处理方式。
     */
    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }
}
