package net.starly.auctionhouse.page;

import net.starly.auctionhouse.entity.AuctionItem;

import java.util.List;

/**
 * AuctionHousePage는 페이지 번호와 페이지에 표시될 아이템 목록을 저장하는 레코드입니다.
 *
 * @param pageNum       경매장의 특정 페이지 번호를 나타냅니다.
 * @param itemStacks    해당 페이지에 표시될 아이템 목록입니다.
 *
 * @since 2023-05-23
 * @author idkNicks
 */
public record AuctionHousePage(int pageNum, List<AuctionItem> itemStacks) {}
