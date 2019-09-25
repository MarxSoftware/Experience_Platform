var gulp = require('gulp');
var concat = require('gulp-concat');
var minify = require('gulp-minify');

var ts = require("gulp-typescript");
var tsProject = ts.createProject("tsconfig.json");

gulp.task("ts-compile", function () {
    return tsProject.src()
        .pipe(tsProject())
        .js.pipe(gulp.dest("build/js"));
});

// verwendet im WordPress Backend
gulp.task('webtools-wp-backend',  function () {
	return gulp.src([
		'build/js/webtools.js',
		'build/js/tools.js',
		'build/js/domready.js',
		'build/js/element.js',
		'build/js/highlight.js',
		'build/js/frontend.js',
	])
		.pipe(concat('webtools-wp-backend.js'))
		.pipe(minify())
		.pipe(gulp.dest('build/dist'));
});

// verwendet im webtools manager
gulp.task('webtools-tracking', function () {
	return gulp.src([
		'build/js/webtools.js',
		'build/js/cookie.js',
		'build/js/tools.js',
		'build/js/domready.js',
		'build/js/element.js',
		'build/js/tracking.js',
	])
		.pipe(concat('webtools-tracking.js'))
		.pipe(minify())
		.pipe(gulp.dest('build/dist'));
});

// verwendet im wordPress Frontend zum clientseitigen Targeting
gulp.task('webtools-frontend', function () {
	return gulp.src([
		'build/js/webtools.js',
		'build/js/frontend.js',
		'build/js/request.js',
		'build/js/highlight.js',
		'build/js/domready.js',
		'build/js/element.js',
	])
		.pipe(concat('webtools-frontend.js'))
		.pipe(minify())
		.pipe(gulp.dest('build/dist'));
});

gulp.task('package-backend', gulp.series('ts-compile', 'webtools-wp-backend'));
gulp.task('package-frontend', gulp.series('ts-compile', 'webtools-frontend'));
gulp.task('package-tracking', gulp.series('ts-compile', 'webtools-tracking'));

gulp.task('package', gulp.series('package-backend', 'package-frontend', 'package-tracking'));

//gulp.task('package', ['webtools-wp-backend', 'webtools-tracking', 'webtools-frontend']);