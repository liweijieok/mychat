package com.example.weixindemo.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;

import com.example.weixindemo.R;
import com.example.weixindemo.bean.ChatEmoji;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FaceConversionUtil {
    /**
     * 每一页表情的个数
     * 21个，是三行七列
     */
    private int pageSize = 20;

    private static FaceConversionUtil mFaceConversionUtil;

    /**
     * 保存于内存中的表情HashMap
     * 对应是名字，描述，比如一个完整的emoji是：
     * emoji_91.png,[外星人54]
     * 则它的一个元素是([外星人],emoji_91)
     */
    private HashMap<String, String> emojiMap = new HashMap<String, String>();

    /**
     * 保存于内存中的表情集合
     */
    private List<ChatEmoji> emojis = new ArrayList<ChatEmoji>();

    /**
     * 表情分页的结果集合
     * 每一页显示20个表情，最后一页显示剩下的
     */
    public List<List<ChatEmoji>> emojiLists = new ArrayList<List<ChatEmoji>>();

    private FaceConversionUtil() {

    }

    public static FaceConversionUtil getInstace() {
        if (mFaceConversionUtil == null) {
            mFaceConversionUtil = new FaceConversionUtil();
        }
        return mFaceConversionUtil;
    }

    /**
     * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
     * 其中str是[外星人]
     * ([外星人],emoji_91)
     *
     * @param context
     * @param str
     * @return
     */
    public SpannableString getExpressionString(Context context, String str) {
        SpannableString spannableString = new SpannableString(str);
//		Toast t=Utils.showToast(context, str, Toast.LENGTH_LONG);
//		t.show();
        // 正则表达式比配字符串里是否含有表情，如： 我好[开心]啊
        //匹配的是第一个是[,最后一个是]
        //中间是不包含]的任意1个以上的字符串
        String zhengze = "\\[[^]]+\\]";
        // 通过传入的正则表达式来生成一个pattern
        Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);
        dealExpression(context, spannableString, sinaPatten, 0);
        return spannableString;
    }

    /**
     * 添加表情
     *
     * @param context
     * @param imgId
     * @param spannableString
     * @return
     */
    public SpannableString addFace(Context context, int imgId,
                                   String spannableString) {
        if (TextUtils.isEmpty(spannableString)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                imgId);
        bitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, true);
        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(spannableString);
        spannable.setSpan(imageSpan, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 添加图片
     *
     * @param context
     * @param bmpUrl          图片路径
     * @param spannableString
     * @return
     */
    public SpannableString addPicture(Context context, String bmpUrl,
                                      String spannableString) {
        if (TextUtils.isEmpty(spannableString)) {
            return null;
        }
//		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
//				imgId);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(bmpUrl, options);
        bitmap = Bitmap.createScaledBitmap(bitmap, 120, 120, true);//图片压缩

        ImageSpan imageSpan = new ImageSpan(context, bitmap);
        SpannableString spannable = new SpannableString(spannableString);
        spannable.setSpan(imageSpan, 0, spannableString.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannable;
    }

    /**
     * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
     * <p/>
     * ([外星人],emoji_91)
     *
     * @param context
     * @param spannableString 是用户发送或者接收的信息
     *                        可能有很多表情，需要递归
     * @param patten
     * @param start
     * @throws Exception
     */
    private void dealExpression(Context context, SpannableString spannableString, Pattern patten, int start) {
        Matcher matcher = patten.matcher(spannableString);
        //存在匹配的
        while (matcher.find()) {
            //在这里判断如果是本地连接就去本地加载图片，
            //如果是图片则显示图片，
            //如果是网络图片这异步加载图片
            String key = matcher.group();
            Log.d("TAG", "key........................." + key);
            Log.d("TAG", "key........................." + key.length());
            if (key.length() <= 7) {
                Log.d("TAG", "发送的是表情");
                // 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
                Log.d("TAG", "matcher.start()" + matcher.start());
                if (matcher.start() < start) {
                    Log.d("TAG", "发送的是表情continue");
                    continue;
                }
                Log.d("TAG", "发送的是表情");
                String value = emojiMap.get(key);
                if (TextUtils.isEmpty(value)) {
                    continue;
                }
                int resId = context.getResources().getIdentifier(value, "drawable",
                        context.getPackageName());
                // 通过上面匹配得到的字符串来生成图片资源id
                if (resId != 0) {
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
                    bitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
                    // 通过图片资源id来得到bitmap，用一个ImageSpan来包装
                    ImageSpan imageSpan = new ImageSpan(bitmap);
                    // 计算该图片名字的长度，也就是要替换的字符串的长度
                    int end = matcher.start() + key.length();
                    // 将该图片替换字符串中规定的位置中
                    spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                }
            }
        }
    }

    /**
     * 读取表情文件
     *
     * @param context
     */
    public void getFileText(Context context) {
        ParseData(FileUtils.getEmojiFile(context), context);
    }

    /**
     * 解析字符
     *
     * @param data 每一个emoji表情的情况
     *             是这样子的
     *             emoji_91.png,[外星人54]
     */
    private void ParseData(List<String> data, Context context) {
        if (data == null) {
            return;
        }
        ChatEmoji emojEentry = null;
        try {
            for (String str : data) {
                String[] text = str.split(",");
                String fileName = text[0].substring(0, text[0].lastIndexOf("."));
                emojiMap.put(text[1], fileName);
                //构造id
                int resID = context.getResources().getIdentifier(fileName, "drawable", context.getPackageName());

                if (resID != 0) {
                    emojEentry = new ChatEmoji();
                    emojEentry.setId(resID);
                    emojEentry.setCharacter(text[1]);
                    emojEentry.setFaceName(fileName);
                    //包含全部
                    emojis.add(emojEentry);
                }
            }
            //计算有多少页
            int pageCount = (int) Math.ceil(emojis.size() / 20f);

            for (int i = 0; i < pageCount; i++) {
                emojiLists.add(getData(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取分页数据
     *
     * @param page 第几页，从0开始
     * @return
     */
    private List<ChatEmoji> getData(int page) {
        int startIndex = page * pageSize;
        int endIndex = startIndex + pageSize;

        //最后一页
        if (endIndex > emojis.size()) {
            endIndex = emojis.size();
        }
        // 初始化，之后把对应的一页的emoji赋值到对应的集合
        List<ChatEmoji> list = new ArrayList<ChatEmoji>();
        list.addAll(emojis.subList(startIndex, endIndex));

        //添加剩下的，但是没有的为null
        if (list.size() < pageSize) {
            for (int i = list.size(); i < pageSize; i++) {
                ChatEmoji object = new ChatEmoji();
                list.add(object);
            }
        }
        //把最后一个设置为删除
        if (list.size() == pageSize) {
            ChatEmoji object = new ChatEmoji();
            object.setId(R.drawable.face_del_icon);
            list.add(object);
        }
        return list;
    }

}
