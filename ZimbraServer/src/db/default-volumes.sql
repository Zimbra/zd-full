-- 
-- 
-- 

INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (1, 1, 'message1', '@ZIMBRA_HOME@store', 12, 8, 12, 8, 0, 4096);
INSERT INTO volume (id, type, name, path, file_bits, file_group_bits,
    mailbox_bits, mailbox_group_bits, compress_blobs, compression_threshold)
  VALUES (2, 10, 'index1', '@ZIMBRA_HOME@index', 12, 8, 12, 8, 0, 4096);

INSERT INTO current_volumes (message_volume_id, index_volume_id, next_mailbox_id) VALUES (1, 2, 1);
