package com.xgn.fzc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * Date: 2018-06-20
 * Time: 3:09 PM
 *
 * @author: MarkFan
 * @since v1.0.0
 */
@Data
@ConfigurationProperties(prefix = "canal")
public class CanalProperties {
    private String server;
    private Integer port;
    private String destination;
    private String username;
    private String password;
}
