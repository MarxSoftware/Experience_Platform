package com.thorstenmarx.webtools.api.ui.sidebar;

/*-
 * #%L
 * webtools-api
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

/**
 * 
 * Icons werden erzeugt
 *
 * Icons are created from https://material.io/icons/: temp1.querySelectorAll(".md-icon").forEach(it => {icons += it.textContent + '("' + it.textContent + '"),'});
 * 
 * Other Source for icons https://materialdesignicons.com/
 * @author marx
 */
public final class Material {

	public enum Icons {

		NONE(""),
		
		threed_rotation("3d_rotation"),
		accessibility("accessibility"),
		accessible("accessible"),
		account_balance("account_balance"),
		account_balance_wallet("account_balance_wallet"),
		account_box("account_box"),
		account_circle("account_circle"),
		add_shopping_cart("add_shopping_cart"),
		alarm("alarm"),
		alarm_add("alarm_add"),
		alarm_off("alarm_off"),
		alarm_on("alarm_on"),
		all_out("all_out"),
		android("android"),
		announcement("announcement"), 
		aspect_ratio("aspect_ratio"), 
		assessment("assessment"), assignment("assignment"), assignment_ind("assignment_ind"), assignment_late("assignment_late"), assignment_return("assignment_return"), assignment_returned("assignment_returned"), assignment_turned_in("assignment_turned_in"), autorenew("autorenew"), backup("backup"), book("book"), bookmark("bookmark"), bookmark_border("bookmark_border"), bug_report("bug_report"), build("build"), cached("cached"), camera_enhance("camera_enhance"), card_giftcard("card_giftcard"), card_membership("card_membership"), card_travel("card_travel"), change_history("change_history"), check_circle("check_circle"), chrome_reader_mode("chrome_reader_mode"), clazz("class"), code("code"), compare_arrows("compare_arrows"), copyright("copyright"), credit_card("credit_card"), dashboard("dashboard"), date_range("date_range"), delete("delete"), delete_forever("delete_forever"), description("description"), dns("dns"), done("done"), done_all("done_all"), donut_large("donut_large"), donut_small("donut_small"), eject("eject"), euro_symbol("euro_symbol"), event("event"), event_seat("event_seat"), exit_to_app("exit_to_app"), explore("explore"), extension("extension"), face("face"), favorite("favorite"), favorite_border("favorite_border"), feedback("feedback"), find_in_page("find_in_page"), find_replace("find_replace"), fingerprint("fingerprint"), flight_land("flight_land"), flight_takeoff("flight_takeoff"), flip_to_back("flip_to_back"), flip_to_front("flip_to_front"), g_translate("g_translate"), gavel("gavel"), get_app("get_app"), gif("gif"), grade("grade"), group_work("group_work"), help("help"), help_outline("help_outline"), highlight_off("highlight_off"), history("history"), home("home"), hourglass_empty("hourglass_empty"), hourglass_full("hourglass_full"), http("http"), https("https"), important_devices("important_devices"), info("info"), info_outline("info_outline"), input("input"), invert_colors("invert_colors"), label("label"), label_outline("label_outline"), language("language"), launch("launch"), lightbulb_outline("lightbulb_outline"), line_style("line_style"), line_weight("line_weight"), list("list"), lock("lock"), lock_open("lock_open"), lock_outline("lock_outline"), loyalty("loyalty"), markunread_mailbox("markunread_mailbox"), motorcycle("motorcycle"), note_add("note_add"), offline_pin("offline_pin"), opacity("opacity"), open_in_browser("open_in_browser"), open_in_new("open_in_new"), open_with("open_with"), pageview("pageview"), pan_tool("pan_tool"), payment("payment"), perm_camera_mic("perm_camera_mic"), perm_contact_calendar("perm_contact_calendar"), perm_data_setting("perm_data_setting"), perm_device_information("perm_device_information"), perm_identity("perm_identity"), perm_media("perm_media"), perm_phone_msg("perm_phone_msg"), perm_scan_wifi("perm_scan_wifi"), pets("pets"), picture_in_picture("picture_in_picture"), picture_in_picture_alt("picture_in_picture_alt"), play_for_work("play_for_work"), polymer("polymer"), power_settings_new("power_settings_new"), pregnant_woman("pregnant_woman"), print("print"), query_builder("query_builder"), question_answer("question_answer"), receipt("receipt"), record_voice_over("record_voice_over"), redeem("redeem"), schedule("schedule"), rowing("rowing"), rounded_corner("rounded_corner"), room("room"), restore_page("restore_page"), restore("restore"), report_problem("report_problem"), reorder("reorder"), remove_shopping_cart("remove_shopping_cart"), search("search"), settings("settings"), settings_applications("settings_applications"), settings_backup_restore("settings_backup_restore"), settings_bluetooth("settings_bluetooth"), settings_brightness("settings_brightness"), settings_cell("settings_cell"), settings_ethernet("settings_ethernet"), settings_input_antenna("settings_input_antenna"), settings_input_component("settings_input_component"), settings_input_composite("settings_input_composite"), settings_input_hdmi("settings_input_hdmi"), settings_input_svideo("settings_input_svideo"), settings_overscan("settings_overscan"), settings_phone("settings_phone"), settings_power("settings_power"), settings_remote("settings_remote"), settings_voice("settings_voice"),
		group("group");

		private final String value;

		private Icons(final String value) {
			this.value = value;
		}

		public String getValue () {
			return value;
		}
	}
}
