package com.xukexiang.house.comment.mapper;

import java.util.List;

import com.xukexiang.house.comment.model.Blog;
import com.xukexiang.house.comment.model.LimitOffset;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


@Mapper
public interface BlogMapper {

  /**
   * 获取Blog列表
   * @param blog
   * @param limitOffset
   * @return
   */
  public List<Blog> selectBlog(@Param("blog") Blog blog, @Param("pageParams") LimitOffset limitOffset);

  /**
   * 查询博客总数
   * @param query
   * @return
   */
  public Long selectBlogCount(Blog query);

}
