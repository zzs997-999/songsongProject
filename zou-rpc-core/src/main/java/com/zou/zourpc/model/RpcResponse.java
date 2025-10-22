package com.zou.zourpc.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

//作用：封装返回得到的返回值
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse implements Serializable {
    private Object data;

    private Class<?>dataType;

    private String message;

    private Exception exception;

}
