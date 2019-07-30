package com.xukexiang.house.comment.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Strings;
import com.xukexiang.house.comment.common.BeanHelper;
import com.xukexiang.house.comment.common.CommonConstants;
import com.xukexiang.house.comment.dao.UserDao;
import com.xukexiang.house.comment.mapper.CommentMapper;
import com.xukexiang.house.comment.model.Comment;
import com.xukexiang.house.comment.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private UserDao userDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取房产评论列表（redis）
     *
     * @param houseId
     * @param size
     * @return
     */
    public List<Comment> getHouseComments(Long houseId, Integer size) {
        String key = "house_comments" + "_" + houseId + "_" + size;
        String json = redisTemplate.opsForValue().get(key);
        List<Comment> lists = null;
        if (Strings.isNullOrEmpty(json)) {
            lists = doGetHouseComments(houseId, size);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(lists));
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        } else {
            lists = JSON.parseObject(json, new TypeReference<List<Comment>>() {
            });
        }
        return lists;
    }

    /**
     * 数据库获取评论列表
     * 调用用户服务获取头像
     *
     * @param houseId
     * @param size
     * @return
     */
    public List<Comment> doGetHouseComments(Long houseId, Integer size) {
        List<Comment> comments = commentMapper.selectComments(houseId, size);
        if (comments != null) {
            comments.forEach(comment -> {
                User user = userDao.getUserDetail(comment.getUserId());
                comment.setAvatar(user.getAvatar());
                comment.setUserName(user.getName());
            });
        }
        return comments;
    }

    /**
     * 获取博客评论
     *
     * @param blogId
     * @param size
     * @return
     */
    public List<Comment> getBlogComments(Integer blogId, Integer size) {
        String key = "blog_comments" + "_" + blogId + "_" + size;
        String json = redisTemplate.opsForValue().get(key);
        List<Comment> lists = null;
        if (Strings.isNullOrEmpty(json)) {
            lists = doGetBlogComments(blogId, size);
            redisTemplate.opsForValue().set(key, JSON.toJSONString(lists));
            redisTemplate.expire(key, 5, TimeUnit.MINUTES);
        } else {
            lists = JSON.parseObject(json, new TypeReference<List<Comment>>() {
            });
        }
        return lists;
    }

    private List<Comment> doGetBlogComments(Integer blogId, Integer size) {
        List<Comment> comments = commentMapper.selectBlogComments(blogId, size);
        if(comments != null) {
            comments.forEach(comment -> {
                User user = userDao.getUserDetail(comment.getUserId());
                comment.setAvatar(user.getAvatar());
                comment.setUserName(user.getName());
            });
        }
        return comments;
    }

    /**
     * 添加房产评论
     *
     * @param houseId
     * @param content
     * @param userId
     */
    public void addHouseComment(Long houseId, String content, Long userId) {
        addComment(houseId, null, content, userId, 1);
    }

    @Transactional(rollbackFor = Exception.class)
    public void addComment(Long houseId, Integer blogId, String content, Long userId, int type) {
        String key = null;
        Comment comment = new Comment();
        if (type == 1) {
            comment.setHouseId(houseId);
            key = "house_comments_" + houseId;
        } else {
            comment.setBlogId(blogId);
            key = "blog_comments_" + blogId;
        }
        comment.setContent(content);
        comment.setUserId(userId);
        comment.setType(type);
        BeanHelper.onInsert(comment);
        BeanHelper.setDefaultProp(comment, Comment.class);
        commentMapper.insert(comment);
        redisTemplate.delete(redisTemplate.keys(key + "*"));
    }

    /**
     * 添加blog评论
     *
     * @param blogId
     * @param content
     * @param userId
     */
    public void addBlogComment(int blogId, String content, Long userId) {
        addComment(null, blogId, content, userId, CommonConstants.COMMENT_BLOG_TYPE);
    }
}
