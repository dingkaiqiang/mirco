package com.xiangxue.jack.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class AccessFilter extends ZuulFilter {
    // 过滤器类型
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
    // 过滤器顺序，值越小 越下线执行  --》 eg： 多个前置过过滤器
    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {
        //获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        //获取Request
        HttpServletRequest request = ctx.getRequest();
        //获取请求参数accessToken
        String accessToken = request.getParameter("accessToken");
        //使用String工具类
        if (StringUtils.isBlank(accessToken)) {
            log.warn("accessToken is empty");
            //设置为false不进行路由
//            ctx.setSendZuulResponse(true);  //进行拦截
////            ctx.setResponseStatusCode(401);
//            try {
////                ctx.getResponse().getWriter().write("accessToken is empty");
//                JSONObject json = new JSONObject();
//                json.put("repCode",302);
//                json.put("repMsg","请求url不合法，非支持平台！！！");
//                getContextForError(ctx,json);
//            } catch (Exception e) {
//            }
            return null;
        }
        log.info("access is ok");
        return null;
    }

    /**
     * 错误拦截
     *
     * @param ctx
     * @param responseModel
     * @return
     */
    private RequestContext getContextForError(RequestContext ctx, JSONObject responseModel) {
        RequestContext requestContext = ctx;
        ctx.setResponseBody(responseModel.toString());
        ctx.setSendZuulResponse(false);
        ctx.getResponse().setContentType("text/html;charset=UTF-8");
        ctx.getResponse().setContentType(String.valueOf(MediaType.APPLICATION_JSON));
        return requestContext;
    }
}
