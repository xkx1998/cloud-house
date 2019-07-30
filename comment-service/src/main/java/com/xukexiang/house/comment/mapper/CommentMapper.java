package com.xukexiang.house.comment.mapper;

import java.util.List;

import com.xukexiang.house.comment.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface CommentMapper {

  /**
   * 添加评论
   * @param comment
   * @return
   */
  int insert(Comment comment);

  /**
   * 获取房产评论列表
   * @param houseId
   * @param size
   * @return
   */
  List<Comment> selectComments(@Param("houseId") long houseId, @Param("size") int size);

  /**
   * 获取Blog的评论列表
   * @param blogId
   * @param size
   * @return
   */
  List<Comment> selectBlogComments(@Param("blogId") long blogId, @Param("size") int size);
  
}

