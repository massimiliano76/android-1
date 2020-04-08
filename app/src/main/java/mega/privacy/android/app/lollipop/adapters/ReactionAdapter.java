package mega.privacy.android.app.lollipop.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mega.privacy.android.app.MegaApplication;
import mega.privacy.android.app.R;
import mega.privacy.android.app.components.twemoji.EmojiRange;
import mega.privacy.android.app.components.twemoji.EmojiUtils;
import mega.privacy.android.app.components.twemoji.ReactionImageView;
import mega.privacy.android.app.components.twemoji.emoji.Emoji;
import mega.privacy.android.app.lollipop.megachat.AndroidMegaChatMessage;
import mega.privacy.android.app.lollipop.megachat.ChatActivityLollipop;
import nz.mega.sdk.MegaChatApiAndroid;
import nz.mega.sdk.MegaHandleList;

import static mega.privacy.android.app.utils.ChatUtil.*;
import static mega.privacy.android.app.utils.Constants.*;
import static mega.privacy.android.app.utils.LogUtil.*;

public class ReactionAdapter extends RecyclerView.Adapter<ReactionAdapter.ViewHolderReaction> implements View.OnClickListener, View.OnLongClickListener{

    private MegaChatApiAndroid megaChatApi;
    private Context context;
    private long messageId;
    private long chatId;
    private ArrayList<String> listReactions;
    private AndroidMegaChatMessage megaMessage;

    public ReactionAdapter(Context context, long chatid, AndroidMegaChatMessage megaMessage, ArrayList<String> listReactions) {
        this.context = context;
        this.listReactions = listReactions;
        this.chatId = chatid;
        this.megaMessage = megaMessage;
        this.messageId = megaMessage.getMessage().getMsgId();
        megaChatApi = MegaApplication.getInstance().getMegaChatApi();
    }

    public class ViewHolderReaction extends RecyclerView.ViewHolder {

        private RelativeLayout moreReactionsLayout;
        private RelativeLayout itemReactionLayout;
        private ReactionImageView itemEmojiReaction;
        private TextView itemNumUsersReaction;
        private Emoji emojiReaction;

        public ViewHolderReaction(View itemView) {
            super(itemView);
        }
    }

    @Override
    public ReactionAdapter.ViewHolderReaction onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reaction, parent, false);
        ViewHolderReaction holder = new ViewHolderReaction(v);
        holder.moreReactionsLayout = v.findViewById(R.id.more_reactions_layout);
        holder.moreReactionsLayout.setTag(holder);
        holder.moreReactionsLayout.setOnClickListener(this);
        holder.itemReactionLayout = v.findViewById(R.id.item_reaction_layout);
        holder.itemReactionLayout.setTag(holder);
        holder.itemReactionLayout.setOnClickListener(this);
        holder.itemNumUsersReaction = v.findViewById(R.id.item_number_users_reaction);
        holder.itemEmojiReaction = v.findViewById(R.id.item_emoji_reaction);

        return holder;
    }

    @Override
    public void onBindViewHolder(ReactionAdapter.ViewHolderReaction holder, int position) {

        String reaction = getItemAtPosition(position);
        if (reaction == null) {
            return;
        }

        if (reaction.equals(INVALID_REACTION)) {
            /* Add more reactions icon visible*/
            holder.moreReactionsLayout.setVisibility(View.VISIBLE);
            holder.itemReactionLayout.setVisibility(View.GONE);
            return;
        }

        /* Specific reaction*/
        holder.moreReactionsLayout.setVisibility(View.GONE);

        List<EmojiRange> emojis = EmojiUtils.emojis(reaction);
        holder.emojiReaction = emojis.get(0).emoji;

        /*Number users*/
        int numUsers = megaChatApi.getMessageReactionCount(chatId, messageId, reaction);
        String text = numUsers+"";
        holder.itemNumUsersReaction.setText(text);

        /*Color background*/
        boolean ownReaction = false;
        MegaHandleList handleList = megaChatApi.getReactionUsers(chatId, messageId, reaction);
        for (int i = 0; i < handleList.size(); i++) {
            if (handleList.get(0) == megaChatApi.getMyUserHandle()) {
                ownReaction = true;
                break;
            }
        }

        if (ownReaction) {
            holder.itemNumUsersReaction.setTextColor(ContextCompat.getColor(context, R.color.stroke_own_reaction_added));
            holder.itemReactionLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.own_reaction_added));
        } else {
            holder.itemNumUsersReaction.setTextColor(ContextCompat.getColor(context, R.color.number_reactions_added));
            holder.itemReactionLayout.setBackground(ContextCompat.getDrawable(context, R.drawable.contact_reaction_added));
        }

        /*Add emoji Reaction*/
        holder.itemEmojiReaction.setEmoji(holder.emojiReaction);
    }

    private String getItemAtPosition(int pos) {
        if (listReactions == null || listReactions.size() == 0 || pos >= listReactions.size())
            return null;

        return listReactions.get(pos);
    }

    @Override
    public int getItemCount() {
        if (listReactions == null)
            return 0;

        return listReactions.size();
    }

    @Override
    public void onClick(View v) {
        ViewHolderReaction holder = (ViewHolderReaction) v.getTag();
        if (holder == null)
            return;

        int currentPosition = holder.getAdapterPosition();
        if (currentPosition < 0) {
            logWarning("Current position error - not valid value");
            return;
        }

        switch (v.getId()) {
            case R.id.more_reactions_layout:
                ((ChatActivityLollipop) context).openReactionBottomSheet(chatId, megaMessage);
                break;

            case R.id.item_reaction_layout:
                addReactionInMsg(context, chatId, megaMessage.getMessage(), holder.emojiReaction);
                break;
        }
    }

    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
