package com.xukexiang.house.comment.controller;

import java.util.List;

import com.xukexiang.house.comment.common.RestResponse;
import com.xukexiang.house.comment.model.Blog;
import com.xukexiang.house.comment.model.BlogQueryReq;
import com.xukexiang.house.comment.model.ListResponse;
import com.xukexiang.house.comment.service.BlogService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("blog")
public class BlogController {
  
  @Autowired
  private BlogService blogService;

  /**
   * 博客列表
   * @param req
   * @return
   */
  @RequestMapping("list")
  public RestResponse<ListResponse<Blog>> list(@RequestBody BlogQueryReq req){
    Pair<List<Blog>,Long> pair = blogService.queryBlog(req.getBlog(),req.getLimit(),req.getOffset());
    return RestResponse.success(ListResponse.build(pair.getKey(), pair.getValue()));
  }

  /**
   * 博客详情
   * @param id
   * @return
   */
  @RequestMapping("one")
  public RestResponse<Blog> one(Integer id){
    Blog blog = blogService.queryOneBlog(id);
    return RestResponse.success(blog);
  }

}