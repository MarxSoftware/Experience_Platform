package com.thorstenmarx.webtools.manager.wicket.components.bootstrap.pagination;

/*-
 * #%L
 * webtools-manager
 * %%
 * Copyright (C) 2016 - 2018 Thorsten Marx
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigation;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationIncrementLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigationLink;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.LoopItem;
import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigation;

/**
 * @author thmarx
 */
public class BootstrapPagingNavigator extends AjaxPagingNavigator {

    public BootstrapPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

    // Link for: "1 | 2 | 3 | 4"
    @Override
    protected PagingNavigation newNavigation(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        return new AjaxPagingNavigation(id, pageable, labelProvider) {
            @Override
            protected LoopItem newItem(int iteration) {
                LoopItem item = super.newItem(iteration);

                // add css for enable/disable link
                long pageIndex = getStartIndex() + iteration;
                item.add(new AttributeModifier("class", new PageLinkCssModel(pageable, pageIndex, "active")));

                return item;
            }
        };
    }

    // Link for: first,last
    @Override
    protected AbstractLink newPagingNavigationLink(String id, IPageable pageable, int pageNumber) {
        ExternalLink navCont = new ExternalLink(id + "Cont", (String) null);

        // add css for enable/disable link
        long pageIndex = pageable.getCurrentPage() + pageNumber;
        navCont.add(new AttributeModifier("class", new PageLinkCssModel(pageable, pageIndex, "disabled")));

        // change original wicket-link, so that it always generates href
        navCont.add(new AjaxPagingNavigationLink(id, pageable, pageNumber) {
            @Override
            protected void disableLink(ComponentTag tag) {
            }
        });
        return navCont;
    }

    // Link for: prev,next
    @Override
    protected AbstractLink newPagingNavigationIncrementLink(String id, IPageable pageable, int increment) {
        ExternalLink navCont = new ExternalLink(id + "Cont", (String) null);

        // add css for enable/disable link
        long pageIndex = pageable.getCurrentPage() + increment;
        navCont.add(new AttributeModifier("class", new PageLinkIncrementCssModel(pageable, pageIndex)));

        // change original wicket-link, so that it always generates href
        navCont.add(new AjaxPagingNavigationIncrementLink(id, pageable, increment) {
            @Override
            protected void disableLink(ComponentTag tag) {
            }
        });
        return navCont;
    }
}
