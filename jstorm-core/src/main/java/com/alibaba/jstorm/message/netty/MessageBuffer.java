/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.jstorm.message.netty;

import java.util.List;

import backtype.storm.messaging.NettyMessage;


/**
 * Encapsulates the state used for batching up messages.
 */
public class MessageBuffer {
    private final int messageBatchSize;
    private MessageBatch currentBatch;

    public MessageBuffer(int messageBatchSize) {
        this.messageBatchSize = messageBatchSize;
        this.currentBatch = new MessageBatch(messageBatchSize);
    }

    public void setMessageBatch(MessageBatch batch) {
        this.currentBatch = batch;
    }

    public MessageBatch add(NettyMessage msg) {
        return add(msg, true);
    }

    /**
     * @param msg        netty message
     * @param isFlushed, true: return batch when batch is full
     *                   false: just add message
     * @return message batch if available
     */
    public MessageBatch add(NettyMessage msg, boolean isFlushed) {
        currentBatch.add(msg);
        if (isFlushed && currentBatch.isFull()) {
            MessageBatch ret = currentBatch;
            currentBatch = new MessageBatch(messageBatchSize);
            return ret;
        } else {
            return null;
        }
    }

    public MessageBatch add(List<NettyMessage> msgs) {
        return add(msgs, true);
    }

    /**
     * @param msgs       a list of netty messages
     * @param isFlushed, true: return batch when batch is full
     *                   false: just add message
     * @return message batch if available
     */
    public MessageBatch add(List<NettyMessage> msgs, boolean isFlushed) {
        currentBatch.add(msgs);
        if (currentBatch.isFull() && isFlushed) {
            MessageBatch ret = currentBatch;
            currentBatch = new MessageBatch(messageBatchSize);
            return ret;
        } else {
            return null;
        }
    }


    public boolean isEmpty() {
        return currentBatch.isEmpty();
    }

    public int size() {
        return currentBatch.getEncodedLength();
    }

    public MessageBatch drain() {
        if (!currentBatch.isEmpty()) {
            MessageBatch ret = currentBatch;
            currentBatch = new MessageBatch(messageBatchSize);
            return ret;
        } else {
            return null;
        }
    }

    public void clear() {
        currentBatch = new MessageBatch(messageBatchSize);
    }
}