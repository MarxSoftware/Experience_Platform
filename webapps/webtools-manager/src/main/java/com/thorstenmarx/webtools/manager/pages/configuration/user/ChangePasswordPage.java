package com.thorstenmarx.webtools.manager.pages.configuration.user;

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
import com.google.inject.Inject;
import com.thorstenmarx.webtools.manager.model.User;
import com.thorstenmarx.webtools.manager.pages.BasePage;
import com.thorstenmarx.webtools.manager.services.UserService;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.feedback.ExactLevelFeedbackMessageFilter;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.validation.EqualPasswordInputValidator;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.PatternValidator;

/**
 *
 * @author marx
 */
public class ChangePasswordPage extends BasePage {

	private static final long serialVersionUID = -5232749494506893053L;
	
	@Inject
	transient UserService userService;
	
	private String oldPassword;
	private String newPassword;
	private String newPasswordConfirm;
	
	private FeedbackPanel errorFeedBack = null;
	private FeedbackPanel successFeedBack = null;
	
	public ChangePasswordPage () {
		super();
		init();
	}
	
	private void displaySuccess () {
		successFeedBack.add(new AttributeModifier("class", "alert alert-success"));
		errorFeedBack.add(new AttributeModifier("class", ""));
	}
	private void displayError () {
		errorFeedBack.add(new AttributeModifier("class", "alert alert-danger"));
		successFeedBack.add(new AttributeModifier("class", ""));
	}
	
	private void clearMessage () {
		errorFeedBack.add(new AttributeModifier("class", ""));
		successFeedBack.add(new AttributeModifier("class", ""));
	}
	
	
	
	private void init () {
		
		errorFeedBack = new FeedbackPanel("feedbackMessage", new ExactLevelFeedbackMessageFilter(FeedbackMessage.ERROR));
		successFeedBack = new FeedbackPanel("succesMessage", new ExactLevelFeedbackMessageFilter(FeedbackMessage.SUCCESS));
		add(errorFeedBack);
		add(successFeedBack);
		
		
		
		final IModel model = new CompoundPropertyModel(this);
		final PasswordTextField oldPasswordField = new PasswordTextField("oldPassword");
		final PasswordTextField newPasswordField = new PasswordTextField("newPassword");
		final PasswordTextField newPasswordFieldConfirm = new PasswordTextField("newPasswordConfirm");
		
		Form<?> form = new Form<Void>("pwdForm") {
			private static final long serialVersionUID = 7283381648682393116L;
			@Override
			protected void onSubmit() {
				String oldpwd = getOldPassword();
				String newpwd = getNewPassword();
				
				User user = userService.login("admin", oldpwd);
				if (user != null) {
					user.password(newpwd);
					userService.add(user);
					displaySuccess();
					success(getString("password.changed"));
				} else {
					displayError();
					error(getString("oldPassword.invalid"));
				}
			}

			@Override
			protected void onValidate() {
				super.onValidate(); //To change body of generated methods, choose Tools | Templates.
				if (hasError()) {
					displayError();
				}
			}
			
		};
		
//		newPasswordField.add(new PatternValidator(PASSWORD_PATTERN));
		oldPasswordField.add(UserExistsValidator.of("admin", userService));
		
		form.add(oldPasswordField);
		form.add(newPasswordField);
		form.add(newPasswordFieldConfirm);
		form.add(new EqualPasswordInputValidator(newPasswordField, newPasswordFieldConfirm));
		
		add(form.setDefaultModel(model));		
		setDefaultModel(model);
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirm() {
		return newPasswordConfirm;
	}

	public void setNewPasswordConfirm(String newPasswordConfirm) {
		this.newPasswordConfirm = newPasswordConfirm;
	}
	
	@Override
	public String getTitle () {
		return getString("pages.password.title");
	}
}
