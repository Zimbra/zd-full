/*
 * 
 */
package com.zimbra.zimbrasync.client.cmd;

import java.io.IOException;
import java.util.Collection;

import com.zimbra.zimbrasync.client.cmd.MoveItems.ItemMoveResponseCallback.ItemMoveError;
import com.zimbra.zimbrasync.wbxml.BinaryCodecException;
import com.zimbra.zimbrasync.wbxml.BinaryParser;
import com.zimbra.zimbrasync.wbxml.BinarySerializer;

/**
 * @author jjzhuang
 */
public class MoveItems extends Command {

    public static interface ItemMoveResponseCallback {
        public static enum ItemMoveError {
            InvalidSrcFld,  //1
            InvalidDstFld,  //2
            SrcDstFldSame,  //4
            ServerError,    //5
            DstItemExists,  //6
            ItemLocked      //7
        };
        
        public void itemMoveError(String srcId, ItemMoveError error) throws CommandCallbackException;
        
        public void itemMoved(String srcId, String dstId) throws CommandCallbackException;
    }
    
    public static class ItemMove {
        private String remoteId;
        private String remoteSrcFldId;
        private String remoteDstFldId;
        
        public ItemMove(String remoteId, String remoteSrcFldId, String remoteDstFldId) {
            this.remoteId = remoteId;
            this.remoteSrcFldId = remoteSrcFldId;
            this.remoteDstFldId = remoteDstFldId;
        }
        
        public String getRemoteId() {
            return remoteId;
        }
        
        public String getRemoteDstFldId() {
            return remoteDstFldId;
        }
        
        public void setRemoteDstFldId(String remoteDstFldId) {
            this.remoteDstFldId = remoteDstFldId;
        }
    }
    
    private ItemMoveResponseCallback callback;
    private Collection<? extends ItemMove> moves;
    
    public MoveItems(Collection<? extends ItemMove> moves, ItemMoveResponseCallback callback) {
        this.moves = moves;
        this.callback = callback;
    }
    
    //<MoveItems>
    //  <Move>
    //    <SrcMsgId>...</SrcMsgId>
    //    <SrcFldId>...</SrcFldId>
    //    <DstFldId>...</DstFldId>
    //  </Move>
    //  ...
    //</MoveItems>
    
    @Override
    protected void encodeRequest(BinarySerializer bs) throws BinaryCodecException, IOException {
        bs.openTag(NAMESPACE_MOVE, MOVE_MOVES);
        for (ItemMove move : moves) {
            bs.openTag(NAMESPACE_MOVE, MOVE_MOVE);
            bs.textElement(NAMESPACE_MOVE, MOVE_SRCMSGID, move.remoteId);
            bs.textElement(NAMESPACE_MOVE, MOVE_SRCFLDID, move.remoteSrcFldId);
            bs.textElement(NAMESPACE_MOVE, MOVE_DSTFLDID, move.remoteDstFldId);
            bs.closeTag(); //Move
        }
        bs.closeTag(); //MoveItems
    }

    //<MoveItems>
    //  <Response>
    //    <SrcMsgId>...</SrcMsgId>
    //    <Status>...</Status>
    //    <DstMsgId>...</DstMsgId>
    //  </Response>
    //  ...
    //</MoveItems>

    @Override
    public void parseResponse(BinaryParser bp) throws CommandCallbackException, BinaryCodecException, IOException {
        bp.openTag(NAMESPACE_MOVE, MOVE_MOVES);
        while (bp.next() == START_TAG && NAMESPACE_MOVE.equals(bp.getNamespace()) && MOVE_RESPONSE.equals(bp.getName())) {
            String srcId = bp.nextTextElement(NAMESPACE_MOVE, MOVE_SRCMSGID);
            int status = bp.nextIntegerElement(NAMESPACE_MOVE, MOVE_STATUS);
            if (status == 3) {
                String dstId = bp.nextTextElement(NAMESPACE_MOVE, MOVE_DSTMSGID);
                callback.itemMoved(srcId, dstId);
            } else {
                callback.itemMoveError(srcId, getError(status));
            }
            bp.closeTag(); //Response
        }
        bp.require(END_TAG, NAMESPACE_MOVE, MOVE_MOVES);
    }
    
    private ItemMoveError getError(int status) throws BinaryCodecException {
        assert status != 3;
        switch (status) {
        case 1:
            return ItemMoveError.InvalidSrcFld;
        case 2:
            return ItemMoveError.InvalidDstFld;
        case 4:
            return ItemMoveError.SrcDstFldSame;
        case 5:
            return ItemMoveError.ServerError;
        case 6:
            return ItemMoveError.DstItemExists;
        case 7:
            return ItemMoveError.ItemLocked;
        default:
            throw new BinaryCodecException("unknown MoveItems response status=" + status);
        }
    }

    @Override
    protected void handleStatusError() throws ResponseStatusException {} //MoveItems doesn't have a global status
}

