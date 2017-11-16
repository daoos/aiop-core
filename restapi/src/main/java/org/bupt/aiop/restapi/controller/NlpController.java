package org.bupt.aiop.restapi.controller;

import com.alibaba.fastjson.JSON;
import org.apache.thrift.protocol.TProtocol;
import org.bupt.aiop.common.bean.ResponseResult;
import org.bupt.aiop.common.thrift.ThriftConnectionService;
import org.bupt.aiop.common.util.Validator;
import org.bupt.aiop.restapi.constant.EnvConsts;
import org.bupt.aiop.restapi.constant.ErrorConsts;
import org.bupt.aiop.restapi.service.RedisService;
import org.bupt.aiop.restapi.service.thrift.NlpAlgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * UserController
 */
@Controller
@RequestMapping("nlp")
public class NlpController {

    private static final Logger logger = LoggerFactory.getLogger(NlpController.class);


    @Autowired
    private RedisService redisService;

    @Autowired
    private EnvConsts envConsts;

    @Autowired
    private ThriftConnectionService thriftNlpConnectionService;


    @RequestMapping(value = "v1/wordseg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseResult wordseg(@RequestBody Map<String, Object> params) {

        /**
         * 1.获取输入
         */
        String text = (String) params.get("text");

        /**
         * 2.校验
         */

        //判空
        if (Validator.checkEmpty(text)) {
            return ResponseResult.error(ErrorConsts.MSG_INVALID_PARAM);
        }

        //判长


        /**
         * 3.处理
         */
        TProtocol protocol = thriftNlpConnectionService.getConnection();
        NlpAlgService.Client nlpAlgSerivce = new NlpAlgService.Client(protocol);

        String res = "";
        try {
            res = nlpAlgSerivce.hello(text);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            thriftNlpConnectionService.returnConnection(protocol);
        }


        return ResponseResult.success(JSON.parseObject(res));
    }

}
