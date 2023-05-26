
package net.starly.auctionhouse.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.starly.auctionhouse.entity.AuctionItemOrStack;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class AuctionHousePage<T extends AuctionItemOrStack> {

    private int pageNum;
    private List<T> itemStacks;
}
