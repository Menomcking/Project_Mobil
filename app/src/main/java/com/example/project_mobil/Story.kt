package com.example.project_mobil

/**
 * Story
 *
 * @property id
 * @property picture
 * @property rating
 * @property title
 * @property description
 * @property storyparts
 * @constructor Create empty Story
 */
class Story(var id: Int, var picture: String, var rating: Int, var title: String, var description: String, var storyparts: List<Storypart>)