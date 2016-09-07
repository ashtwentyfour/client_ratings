-- phpMyAdmin SQL Dump
-- version 4.6.4
-- https://www.phpmyadmin.net/
--
-- Host: db
-- Generation Time: Sep 07, 2016 at 05:25 AM
-- Server version: 5.7.14
-- PHP Version: 5.6.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `client_ratings`
--

-- --------------------------------------------------------

--
-- Table structure for table `assessments`
--

CREATE TABLE `assessments` (
  `assess_id` int(50) NOT NULL,
  `user_id` int(50) NOT NULL,
  `client_id` int(50) NOT NULL,
  `assess_date` date NOT NULL DEFAULT '1990-10-03',
  `survey_name` varchar(150) DEFAULT NULL,
  `instructions` varchar(255) DEFAULT NULL,
  `total_score` double NOT NULL DEFAULT '-1',
  `global_rel_score` double NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `assessments`
--

INSERT INTO `assessments` (`assess_id`, `user_id`, `client_id`, `assess_date`, `survey_name`, `instructions`, `total_score`, `global_rel_score`) VALUES
(1, 1, 1, '2015-12-01', 'survey_1', 'take the survey', 1.35, 35.05037136444091),
(2, 2, 2, '2015-10-14', 'survey_2', 'take the survey', 2.35, 64.94962863555908),
(8, 1, 2, '2015-10-10', NULL, NULL, -1, -1);

-- --------------------------------------------------------

--
-- Table structure for table `assessment_score`
--

CREATE TABLE `assessment_score` (
  `assess_id` int(50) NOT NULL,
  `dom_score` double NOT NULL,
  `rel_score` double NOT NULL,
  `domain_id` int(11) DEFAULT NULL,
  `assessment_score_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `avg_score`
--

CREATE TABLE `avg_score` (
  `assessment_score_id` int(11) DEFAULT NULL,
  `industry_domain_avg` double DEFAULT NULL,
  `industry_cum_avg` double DEFAULT NULL,
  `avg_score_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `client_id` int(255) NOT NULL,
  `rater_id` int(100) NOT NULL DEFAULT '1',
  `client_name` varchar(50) NOT NULL,
  `client_industry` varchar(50) NOT NULL,
  `parent_company` varchar(75) DEFAULT NULL,
  `client_division` varchar(50) NOT NULL,
  `client_location` varchar(255) NOT NULL DEFAULT 'USA',
  `industry_size` int(255) NOT NULL DEFAULT '-1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `client`
--

INSERT INTO `client` (`client_id`, `rater_id`, `client_name`, `client_industry`, `parent_company`, `client_division`, `client_location`, `industry_size`) VALUES
(5, 1, 'Apple', 'Consumer Electronics', 'Apple', 'Hardware', 'USA', 2000),
(1, 1, 'General Electric', 'Electricity', 'General Electric Holding Company', 'Hardware', 'USA', 20000),
(2, 2, 'NationalGrid', 'Electricity', 'NG', 'Hardware', 'USA', 100000);

-- --------------------------------------------------------

--
-- Table structure for table `domain`
--

CREATE TABLE `domain` (
  `domain_id` int(10) NOT NULL,
  `assess_id` int(10) NOT NULL,
  `domain_name` varchar(50) NOT NULL,
  `domain_explanation` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `domain`
--

INSERT INTO `domain` (`domain_id`, `assess_id`, `domain_name`, `domain_explanation`) VALUES
(1, 1, 'physical', 'physical security'),
(1, 2, 'physical', 'physical security'),
(1, 8, 'physical security', 'physical security');

-- --------------------------------------------------------

--
-- Table structure for table `domain_info`
--

CREATE TABLE `domain_info` (
  `domain_id` int(10) NOT NULL,
  `domain_name` varchar(255) NOT NULL,
  `domain_description` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `domain_info`
--

INSERT INTO `domain_info` (`domain_id`, `domain_name`, `domain_description`) VALUES
(1, 'physical security', 'physical security'),
(2, 'Web Security', 'Web Security');

-- --------------------------------------------------------

--
-- Table structure for table `questions`
--

CREATE TABLE `questions` (
  `question_id` int(10) NOT NULL,
  `domain_id` int(10) NOT NULL,
  `question_text` varchar(225) NOT NULL,
  `question_rank` double NOT NULL DEFAULT '0.5',
  `dependent_question_id` int(10) NOT NULL DEFAULT '-1',
  `dependent_question_text` varchar(225) NOT NULL DEFAULT '""'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `questions`
--

INSERT INTO `questions` (`question_id`, `domain_id`, `question_text`, `question_rank`, `dependent_question_id`, `dependent_question_text`) VALUES
(1, 1, 'Where is your firm located ?', 0.5, 1, ''),
(2, 1, 'When was your firm established ?', 0.35, 2, ''),
(10, 2, 'How secure is the web security ?', 0.2, -1, '""'),
(11, 2, 'What is the web ?', 0.1, -1, '""');

-- --------------------------------------------------------

--
-- Table structure for table `raters`
--

CREATE TABLE `raters` (
  `rater_id` int(100) NOT NULL,
  `rater_firstname` varchar(50) NOT NULL,
  `rater_lastname` varchar(255) NOT NULL,
  `rater_position` varchar(50) NOT NULL,
  `rater_username` varchar(75) NOT NULL,
  `rater_password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `raters`
--

INSERT INTO `raters` (`rater_id`, `rater_firstname`, `rater_lastname`, `rater_position`, `rater_username`, `rater_password`) VALUES
(1, 'user_1', '', 'client_rater', 'user_1', 'user_1'),
(2, 'user_2', '', 'client_rater', 'user_2', 'user_2'),
(3, 'user_3', '', 'client_rater', 'user_3', 'user_3');

-- --------------------------------------------------------

--
-- Table structure for table `responses`
--

CREATE TABLE `responses` (
  `response_id` int(10) NOT NULL,
  `question_id` int(10) NOT NULL,
  `assess_id` int(100) NOT NULL,
  `answer_numeric` int(10) NOT NULL,
  `comments` varchar(225) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `responses`
--

INSERT INTO `responses` (`response_id`, `question_id`, `assess_id`, `answer_numeric`, `comments`) VALUES
(1, 1, 1, 2, 'n/a'),
(2, 2, 1, 1, 'n/a'),
(3, 1, 2, 4, 'n/a'),
(4, 2, 2, 1, 'n/a');

-- --------------------------------------------------------

--
-- Table structure for table `risks`
--

CREATE TABLE `risks` (
  `risk_id` int(10) NOT NULL,
  `assess_id` int(10) NOT NULL,
  `risk_explanation` varchar(225) NOT NULL,
  `risk_mitigation_comments` varchar(225) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `user_id` int(100) NOT NULL,
  `client_id` int(100) NOT NULL,
  `user_title` varchar(50) NOT NULL,
  `user_email` varchar(50) NOT NULL,
  `user_firstname` varchar(75) NOT NULL,
  `user_lastname` varchar(75) NOT NULL,
  `username` varchar(50) NOT NULL,
  `user_password` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`user_id`, `client_id`, `user_title`, `user_email`, `user_firstname`, `user_lastname`, `username`, `user_password`) VALUES
(1, 1, 'Senior Client Analyst', 'bob@spam.com', 'Bob', 'Spam', 'bobspam', 'bobspam'),
(2, 2, 'Senior Engineer', 'mark@gspam.com', 'Mark', 'Carlson', 'mark.carlson', 'mark');

-- --------------------------------------------------------

--
-- Table structure for table `user_assessments`
--

CREATE TABLE `user_assessments` (
  `user_assessments_id` int(11) NOT NULL,
  `assess_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `assessments`
--
ALTER TABLE `assessments`
  ADD PRIMARY KEY (`assess_id`,`client_id`),
  ADD KEY `assessments_ibfk_1` (`user_id`),
  ADD KEY `assessments_ibfk_2` (`client_id`),
  ADD KEY `assess_date` (`assess_date`);

--
-- Indexes for table `assessment_score`
--
ALTER TABLE `assessment_score`
  ADD PRIMARY KEY (`assessment_score_id`),
  ADD KEY `assessment_score_ibfk_1` (`assess_id`);

--
-- Indexes for table `avg_score`
--
ALTER TABLE `avg_score`
  ADD PRIMARY KEY (`avg_score_id`),
  ADD UNIQUE KEY `assessment_score_id` (`assessment_score_id`),
  ADD KEY `fk_assessment_score_id` (`assessment_score_id`);

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`client_name`,`client_division`),
  ADD KEY `del_user_fk` (`rater_id`),
  ADD KEY `client_id` (`client_id`);

--
-- Indexes for table `domain`
--
ALTER TABLE `domain`
  ADD PRIMARY KEY (`domain_id`,`assess_id`),
  ADD KEY `domain_ibfk_1` (`assess_id`);

--
-- Indexes for table `domain_info`
--
ALTER TABLE `domain_info`
  ADD PRIMARY KEY (`domain_name`),
  ADD UNIQUE KEY `domain_id` (`domain_id`);

--
-- Indexes for table `questions`
--
ALTER TABLE `questions`
  ADD PRIMARY KEY (`question_id`),
  ADD KEY `questions_ibfk_1` (`domain_id`);

--
-- Indexes for table `raters`
--
ALTER TABLE `raters`
  ADD PRIMARY KEY (`rater_id`);

--
-- Indexes for table `responses`
--
ALTER TABLE `responses`
  ADD PRIMARY KEY (`response_id`),
  ADD KEY `responses_ibfk_1` (`question_id`);

--
-- Indexes for table `risks`
--
ALTER TABLE `risks`
  ADD PRIMARY KEY (`risk_id`),
  ADD KEY `risks_ibfk_1` (`assess_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`user_id`),
  ADD KEY `user_ibfk_1` (`client_id`);

--
-- Indexes for table `user_assessments`
--
ALTER TABLE `user_assessments`
  ADD PRIMARY KEY (`user_assessments_id`),
  ADD KEY `assess_id` (`assess_id`),
  ADD KEY `user_assessments_ibfk_1` (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `assessments`
--
ALTER TABLE `assessments`
  MODIFY `assess_id` int(50) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;
--
-- AUTO_INCREMENT for table `assessment_score`
--
ALTER TABLE `assessment_score`
  MODIFY `assessment_score_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `avg_score`
--
ALTER TABLE `avg_score`
  MODIFY `avg_score_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `client_id` int(255) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;
--
-- AUTO_INCREMENT for table `questions`
--
ALTER TABLE `questions`
  MODIFY `question_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;
--
-- AUTO_INCREMENT for table `raters`
--
ALTER TABLE `raters`
  MODIFY `rater_id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `responses`
--
ALTER TABLE `responses`
  MODIFY `response_id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=86;
--
-- AUTO_INCREMENT for table `risks`
--
ALTER TABLE `risks`
  MODIFY `risk_id` int(10) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `user_id` int(100) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
--
-- AUTO_INCREMENT for table `user_assessments`
--
ALTER TABLE `user_assessments`
  MODIFY `user_assessments_id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `assessments`
--
ALTER TABLE `assessments`
  ADD CONSTRAINT `assessments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_client_id` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`),
  ADD CONSTRAINT `fk_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`);

--
-- Constraints for table `assessment_score`
--
ALTER TABLE `assessment_score`
  ADD CONSTRAINT `assessment_score_ibfk_1` FOREIGN KEY (`assess_id`) REFERENCES `assessments` (`assess_id`) ON UPDATE CASCADE;

--
-- Constraints for table `client`
--
ALTER TABLE `client`
  ADD CONSTRAINT `del_user_fk` FOREIGN KEY (`rater_id`) REFERENCES `raters` (`rater_id`) ON UPDATE CASCADE;

--
-- Constraints for table `domain`
--
ALTER TABLE `domain`
  ADD CONSTRAINT `domain_ibfk_1` FOREIGN KEY (`assess_id`) REFERENCES `assessments` (`assess_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain_info` (`domain_id`);

--
-- Constraints for table `responses`
--
ALTER TABLE `responses`
  ADD CONSTRAINT `responses_ibfk_1` FOREIGN KEY (`question_id`) REFERENCES `questions` (`question_id`) ON UPDATE CASCADE;

--
-- Constraints for table `risks`
--
ALTER TABLE `risks`
  ADD CONSTRAINT `risks_ibfk_1` FOREIGN KEY (`assess_id`) REFERENCES `responses` (`response_id`) ON UPDATE CASCADE;

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `fk_client_id_2` FOREIGN KEY (`client_id`) REFERENCES `client` (`client_id`);

--
-- Constraints for table `user_assessments`
--
ALTER TABLE `user_assessments`
  ADD CONSTRAINT `user_assessments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `user_assessments_ibfk_2` FOREIGN KEY (`assess_id`) REFERENCES `assessments` (`assess_id`) ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
